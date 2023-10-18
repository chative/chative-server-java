package org.whispersystems.textsecuregcm.internal;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.SharedMetricRegistries;
import com.github.difftim.eslogger.ESLogger;
import com.google.gson.Gson;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.configuration.EmailConfiguration;
import org.whispersystems.textsecuregcm.configuration.InternalTimedTaskConfiguration;
import org.whispersystems.textsecuregcm.email.SMTPClient;
import org.whispersystems.textsecuregcm.entities.OutgoingMessageEntityForRemind;
import org.whispersystems.textsecuregcm.entities.OutgoingMessageEntityListForRemind;
import org.whispersystems.textsecuregcm.internal.common.BaseResponse;
import org.whispersystems.textsecuregcm.internal.common.Empty;
import org.whispersystems.textsecuregcm.internal.common.STATUS;
import org.whispersystems.textsecuregcm.internal.timedtask.*;
import org.whispersystems.textsecuregcm.storage.*;
import org.whispersystems.textsecuregcm.util.Constants;
import org.whispersystems.textsecuregcm.util.StringUtil;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;

public class InternalTimedTaskServiceImpl extends InternalTimedTaskServiceGrpc.InternalTimedTaskServiceImplBase {
    private final Logger logger = LoggerFactory.getLogger(InternalTimedTaskServiceImpl.class);

    final private AccountsManager accountsManager;
    final private MessagesManager messagesManager;
    final private GroupManagerWithTransaction groupManagerWithTransaction;
    final private InternalTimedTaskConfiguration internalTimedTaskConfiguration;
    final private EmailConfiguration emailConfiguration;
    ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 20, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new ThreadPoolExecutor.CallerRunsPolicy());

    public InternalTimedTaskServiceImpl(AccountsManager accountsManager, MessagesManager messagesManager, GroupManagerWithTransaction groupManagerWithTransaction, InternalTimedTaskConfiguration internalTimedTaskConfiguration, EmailConfiguration emailConfiguration) {
        this.accountsManager = accountsManager;
        this.messagesManager = messagesManager;
        this.groupManagerWithTransaction = groupManagerWithTransaction;
        this.internalTimedTaskConfiguration = internalTimedTaskConfiguration;
        this.emailConfiguration = emailConfiguration;
        SharedMetricRegistries.getOrCreate(Constants.METRICS_NAME)
                .register(name(InternalTimedTaskServiceImpl.class, "InternalTimedTaskServiceImpl_executor_depth"),
                        (Gauge<Long>) ((ThreadPoolExecutor)executor)::getTaskCount);
    }

    @Override
    public void clearExpiredMsg(Empty request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder result = BaseResponse.newBuilder();
        result.setVer(1);
        if (internalTimedTaskConfiguration.getMessageExpireThreshold() > 0) {
            long count=messagesManager.trimMessage(internalTimedTaskConfiguration.getMessageExpireThreshold());
            result.setStatus(STATUS.OK_VALUE);
            result.setReason("delete "+count+" rows");
        } else {
            result.setStatus(STATUS.OTHER_ERROR_VALUE);
            result.setReason("clearExpiredMsg error! invalid config! messageExpireThreshold:"+internalTimedTaskConfiguration.getMessageExpireThreshold());
            logger.error("clearExpiredMsg error! invalid config! messageExpireThreshold:{}", internalTimedTaskConfiguration.getMessageExpireThreshold());
        }
        responseObserver.onNext(result.build());
        responseObserver.onCompleted();
    }

    @Override
    public void clearExpiredMsgForRequest(ExpiredMsgRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder result = BaseResponse.newBuilder();
        result.setVer(1);
        if(request==null||(StringUtil.isEmpty(request.getSourceRegex())&&request.getMessageExpireThreshold()<7)
                ||(!StringUtil.isEmpty(request.getSourceRegex())&&request.getMessageExpireThreshold()<5)) {
            result.setStatus(STATUS.OTHER_ERROR_VALUE);
            result.setReason("clearExpiredMsg error! invalid param! sourceRegex:"+ (request==null?"request is null":request.hasSourceRegex()?request.getSourceRegex():"")+" messageExpireThreshold:"+(request==null?"request is null":request.hasMessageExpireThreshold()?request.getMessageExpireThreshold():0));
            logger.error("clearExpiredMsg error! invalid config! sourceRegex:{} ,messageExpireThreshold:{}", request==null?"request is null":request.hasSourceRegex()?request.getSourceRegex():"", request==null?"request is null":request.hasMessageExpireThreshold()?request.getMessageExpireThreshold():0);
        }else {
            if (StringUtil.isEmpty(request.getSourceRegex())) {
                long count=messagesManager.trimMessage(request.getMessageExpireThreshold());
                result.setStatus(STATUS.OK_VALUE);
                result.setReason("delete "+count+" rows");
            }else{
                long count=messagesManager.trimMessage(request.getSourceRegex(),request.getMessageExpireThreshold());
                result.setReason("delete "+count+" rows");
                result.setStatus(STATUS.OK_VALUE);
            }
        }

        responseObserver.onNext(result.build());
        responseObserver.onCompleted();
    }

    @Override
    public void clearInactiveGroups(Empty request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder result = BaseResponse.newBuilder();
        result.setVer(1);
        if (internalTimedTaskConfiguration.getGroupExpireThreshold() > 0) {
            groupManagerWithTransaction.deleteNotActiveGroup(internalTimedTaskConfiguration.getGroupExpireThreshold());
            result.setStatus(STATUS.OK_VALUE);
        } else {
            result.setStatus(STATUS.OTHER_ERROR_VALUE);
            result.setReason("clearExpiredMsg error! invalid config! groupExpireThreshold:"+internalTimedTaskConfiguration.getGroupExpireThreshold());
            logger.error("clearExpiredMsg error! invalid config! groupExpireThreshold:{}", internalTimedTaskConfiguration.getGroupExpireThreshold());
        }
        responseObserver.onNext(result.build());
        responseObserver.onCompleted();
    }

    @Override
    public void messageNotReceivedRemind(Empty request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder result = BaseResponse.newBuilder();
        result.setVer(1);
        Long end = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(internalTimedTaskConfiguration.getMessageRemindBeforeDaysEnd());
        Long begin = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(internalTimedTaskConfiguration.getMessageRemindBeforeDaysStart());
        int page = 0;
        Map<String, Set<String>> remindMap = new HashMap<>();
        getRemind(remindMap, begin, end, page);
        //sendRemind(remindMap,begin);
        result.setStatus(STATUS.OK_VALUE);
        responseObserver.onNext(result.build());
        responseObserver.onCompleted();
    }

    @Override
    public void inactiveAccountRemind(InactiveRemindRequest request, StreamObserver<BaseResponse> responseObserver) {
        //BaseResponse.Builder result = BaseResponse.newBuilder();
        //result.setVer(1);
        //if (request!=null&&request.getRemindThresholdsCount() > 0) {
        //    for(int accountInactiveRemindThreshold:request.getRemindThresholdsList()) {
        //        try {
        //            if(accountInactiveRemindThreshold<30){
        //                continue;
        //            }
        //            List<Account> accounts = accountsManager.getInactiveAccount(accountInactiveRemindThreshold);
        //            if(accounts.size()>0)
        //            executor.submit(new Runnable() {
        //                @Override
        //                public void run() {
        //                    for(Account account:accounts){
        //                        if(!StringUtil.isEmpty(account.getEmail())) {
        //                            sendInactiveRemindEmail(account, request.getEmailSubject(), request.getEmailTemplatate(), accountInactiveRemindThreshold);
        //                        }else{
        //                            logger.warn("inactiveAccountRemind  email is null, account:{},accountInactiveRemindThreshold:{}",account.getNumber(),accountInactiveRemindThreshold);
        //                        }
        //                    }
        //                }
        //            });
        //        } catch (ParseException e) {
        //            logger.error("inactiveAccountRemind accountsManager.getInactiveAccount error:{}", e.getMessage());
        //        }
        //    }
        //    result.setStatus(STATUS.OK_VALUE);
        //} else {
        //    result.setStatus(STATUS.INVALID_PARAMETER_VALUE);
        //    result.setReason("inactiveAccountRemind error! param error:"+request);
        //    logger.error("inactiveAccountRemind error! param error:{}", request);
        //}
        //responseObserver.onNext(result.build());
        //responseObserver.onCompleted();
    }

    @Override
    public void hiddenNotActiveAccount(InactiveRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder result = BaseResponse.newBuilder();
        result.setVer(1);
        boolean isExecute=false;
        int accountExpireThreshold=0;
        if(request!=null){
            if(request.hasIsExecute()){
                isExecute = request.getIsExecute();
            }
            if(request.hasAccountExpireThreshold()){
                accountExpireThreshold = request.getAccountExpireThreshold();
            }
        }

        List<String> inactiveNumbers= accountsManager.inActiveAccountHandle(isExecute,accountExpireThreshold);
        result.setStatus(STATUS.OK_VALUE);
        result.setReason("isExecute:"+isExecute+",size:"+inactiveNumbers.size()+",inactiveNumbers:"+new Gson().toJson(inactiveNumbers));
        responseObserver.onNext(result.build());
        responseObserver.onCompleted();
    }

    @Override
    public void notifyMerge(NotifyRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder result = BaseResponse.newBuilder();
        result.setVer(1);
        int notifyCountThreshold=10;
        if(request!=null&&request.hasNotifyCountThreshold()&&request.getNotifyCountThreshold()>1){
            notifyCountThreshold=request.getNotifyCountThreshold();
        }
        long begin=System.currentTimeMillis();
        long count=messagesManager.notifyMerge(notifyCountThreshold);
        result.setStatus(STATUS.OK_VALUE);
        result.setReason("noyifyMerge count:"+count+" cost:"+(System.currentTimeMillis()-begin));
        responseObserver.onNext(result.build());
        responseObserver.onCompleted();
    }


    //private void sendRemind(Map<String, Set<String>> remindMap,long begin) {
    //    if (remindMap == null || remindMap.size() == 0) {
    //        return;
    //    }
    //    for (String number : remindMap.keySet()) {
    //        executor.submit(new Runnable() {
    //            @Override
    //            public void run() {
    //                Set<String> sources = remindMap.get(number);
    //                if (sources == null || sources.size() == 0) {
    //                    return;
    //                }
    //                Optional<Account> accountOptional = accountsManager.get(number);
    //                StringBuffer validSourcesSb = new StringBuffer();
    //                String firstSource=null;
    //                if (accountOptional.isPresent() && !StringUtil.isEmpty(accountOptional.get().getEmail())) {
    //                    if (!accountsManager.isAcceptRemind(number)) return;
    //                    if (accountsManager.isActiveForTime(accountOptional.get(), begin)) return;
    //                    for (String source : sources) {
    //                        if( number.length()!=12){
    //                            continue;
    //                        }
    //                        Optional<Account> sourceOptional = accountsManager.get(source);
    //                        if (sourceOptional.isPresent() && !sourceOptional.get().isinValid() && accountsManager.isActive(sourceOptional.get())) {
    //                            String name=sourceOptional.get().getPlainName()==null?sourceOptional.get().getNumber():sourceOptional.get().getPlainName();
    //                            if(firstSource==null){
    //                                firstSource=name;
    //                            }
    //                            validSourcesSb.append(name);
    //                            validSourcesSb.append(",");
    //                        }
    //                    }
    //                }
    //                if (validSourcesSb.length() == 0) return;
    //                String validSources = validSourcesSb.substring(0, validSourcesSb.length() - 1);
    //
    //                sendRemindEmail(accountOptional.get(), validSources,firstSource);
    //            }
    //        });
    //    }
    //}

    //private void sendRemindEmail(Account account, String validSources,String firstSource) {
    //    String encryptNumber=accountsManager.getEncryptNumber(account.getNumber());
    //    if(StringUtil.isEmpty(encryptNumber)){
    //        logger.error("sendRemindEmail getEncryptNumber error! number:{}",account.getNumber());
    //        return;
    //    }
    //    String emailSubject=internalTimedTaskConfiguration.getEmailSubject().replace("#firstSrouce#",firstSource);
    //
    //    String content= null;
    //    try {
    //        content = internalTimedTaskConfiguration.getEmailTemplatate()
    //                .replace("#destinationName#",account.getPlainName()==null?account.getNumber():account.getPlainName())
    //                .replace("#validSources#",validSources)
    //                .replace("#number#", URLEncoder.encode(encryptNumber,"UTF-8"));
    //    } catch (UnsupportedEncodingException e) {
    //        logger.error(e.toString());
    //    }
    //    String address=account.getEmail();
    //    try {
    //        SMTPClient.sendHtml(
    //                emailConfiguration.getServer(),
    //                emailConfiguration.getPort(),
    //                emailConfiguration.getFrom(),
    //                emailConfiguration.getUsername(),
    //                emailConfiguration.getPassword(),
    //                Arrays.asList(address),
    //                emailSubject,
    //                content
    //        );
    //        ESLogger logger = new ESLogger("msgreminds");
    //        logger.withCustom("validSources",validSources);
    //        logger.withUID(account.getNumber());
    //        logger.withCustom("operator","sendEmail");
    //        logger.withCustom("type","msgEmailRemind");
    //        logger.send();
    //    } catch (MessagingException e) {
    //        logger.error(e.toString());
    //    }
    //}

    private void getRemind(Map<String, Set<String>> remindMap, long begin, long end, int page) {
        OutgoingMessageEntityListForRemind outgoingMessageEntityList = messagesManager.getMessagesForTimeRange(begin, end, page * Messages.RESULT_SET_CHUNK_SIZE);
        if (outgoingMessageEntityList != null) {
            if (outgoingMessageEntityList.getMessages() != null && outgoingMessageEntityList.getMessages().size() > 0) {
                for (OutgoingMessageEntityForRemind outgoingMessageEntity : outgoingMessageEntityList.getMessages()) {
                    if (remindMap.containsKey(outgoingMessageEntity.getDestination())) {
                        remindMap.get(outgoingMessageEntity.getDestination()).add(outgoingMessageEntity.getSource());
                    } else {
                        Set<String> sources = new HashSet<>();
                        sources.add(outgoingMessageEntity.getSource());
                        remindMap.put(outgoingMessageEntity.getDestination(), sources);
                    }
                }
                if (outgoingMessageEntityList.hasMore()) {
                    page = page + 1;
                    getRemind(remindMap, begin, end, page);
                }
            }
        }
    }

    @Override
    public void groupCycleRemind(GroupRemindRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder result = BaseResponse.newBuilder();
        result.setVer(1);
        if (request==null||StringUtil.isEmpty(request.getRemindCycle())|| GroupsTable.RemindCycle.fromValue(request.getRemindCycle())==null||GroupsTable.RemindCycle.NONE.getValue().equals(request.getRemindCycle())) {
            result.setStatus(STATUS.INVALID_PARAMETER_VALUE);
            result.setReason("remindCycle param error!");
        } else {
           executor.submit(new Runnable() {
               @Override
               public void run() {
                   groupManagerWithTransaction.groupCycleRemind(request.getRemindCycle());
               }
           });
            result.setStatus(STATUS.OK_VALUE);
        }
        responseObserver.onNext(result.build());
        responseObserver.onCompleted();
    }

    public static void main(String[] args) {
        StringBuffer validSourcesSb = new StringBuffer();
        validSourcesSb.append("adsfadf").append("/");
        String validSources = validSourcesSb.substring(0, validSourcesSb.length() - 1);
        System.out.println(validSources);
    }

}
