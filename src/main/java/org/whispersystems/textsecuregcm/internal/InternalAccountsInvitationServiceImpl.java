package org.whispersystems.textsecuregcm.internal;

import com.google.protobuf.Any;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.StringUtils;
import org.whispersystems.textsecuregcm.InternalAccount.InvitationsManager;
import org.whispersystems.textsecuregcm.internal.common.BaseResponse;
import org.whispersystems.textsecuregcm.internal.common.STATUS;
import org.whispersystems.textsecuregcm.internal.common.Step;
import org.whispersystems.textsecuregcm.internal.invitation.*;
import org.whispersystems.textsecuregcm.storage.InternalAccountsInvitationRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InternalAccountsInvitationServiceImpl extends InternalAccountsInvitationServiceGrpc.InternalAccountsInvitationServiceImplBase {

    final private InvitationsManager invitationsManager;

    public InternalAccountsInvitationServiceImpl(InvitationsManager invitationsManager) {
        this.invitationsManager = invitationsManager;
    }

    @Override
    public void getAll(InvitationRequest request, StreamObserver<InvitationResponse> responseObserver) {

        Step step = request.getStep();
        InvitationUpdateRequest invitation = request.getInvitation();
        InvitationResponse.Builder responseBuilder = InvitationResponse.newBuilder();
        int offset = step.getOffset();
        int limit = step.getLength();

        List<InternalAccountsInvitationRow> invitationList = invitationsManager.getInvitationList(offset, limit, invitation.getEmail(), invitation.getName(), invitation.getNumber(), invitation.getCode(), invitation.getInviter());
        long invitationTotal = invitationsManager.getInvitationTotal(invitation.getEmail(), invitation.getName(), invitation.getNumber(), invitation.getCode(), invitation.getInviter());

        responseBuilder.setTotal(invitationTotal);
        if (!invitationList.isEmpty()) {
            for(InternalAccountsInvitationRow row : invitationList) {
                InvitationResponse.InvitationInfo.Builder builder = InvitationResponse.InvitationInfo.newBuilder();
                builder.setAccount(StringUtils.isEmpty(row.getAccount())?"":row.getAccount());
                builder.setRegisterTime(row.getRegister_time());
                builder.setCode(row.getCode());
                builder.setEmail(StringUtils.isEmpty(row.getEmailHash())?"":row.getEmailHash());
                builder.setInviter(StringUtils.isEmpty(row.getInviter())?"":row.getInviter());
                builder.setName(StringUtils.isEmpty(row.getName())?"":row.getName());
                builder.setOrgs(row.getOrgs() == null? "" : row.getOrgs());
                builder.setTimestamp(row.getTimestamp());
                responseBuilder.addInvitationInfo(builder.build());
            }

        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    //@Override
    //public void generateInvitationCode(GenerateInvitationCode request, StreamObserver<BaseResponse> responseObserver) {
    //
    //    List<String> teams = new ArrayList<>();
    //    teams.add(request.getOrgs());
    //    String code = invitationsManager.generateInvitationCode(Optional.ofNullable(request.getNumber()), Optional.ofNullable(request.getName()),
    //            Optional.ofNullable(request.getEmail().toLowerCase()), Optional.ofNullable(teams), Optional.ofNullable(request.getInviter()), Optional.ofNullable(request.getOktaId()), Optional.ofNullable(request.getOktaOrg()));
    //
    //    BaseResponse.Builder builder = BaseResponse.newBuilder();
    //    builder.setStatus(STATUS.OK_VALUE);
    //    builder.setVer(1);
    //
    //    GenerateInvitationCodeRes.Builder codeRes = GenerateInvitationCodeRes.newBuilder();
    //    codeRes.setCode(code);
    //    builder.setData(Any.pack(codeRes.build()));
    //
    //    responseObserver.onNext(builder.build());
    //    responseObserver.onCompleted();
    //}

    @Override
    public void update(InvitationUpdateRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder builder = BaseResponse.newBuilder();
        builder.setVer(1);

        if(StringUtils.isBlank(request.getOrgs()) || StringUtils.isBlank(request.getCode())){
            builder.setStatus(STATUS.INVALID_PARAMETER_VALUE);
        } else {
            List<InternalAccountsInvitationRow> internalAccountsInvitationRows = invitationsManager.get(request.getCode());
            if(internalAccountsInvitationRows.size() != 0){
                InternalAccountsInvitationRow internalAccountsInvitationRow = internalAccountsInvitationRows.get(0);
                String inviter = request.getInviter();
                if(StringUtils.isBlank(inviter)){
                    inviter = internalAccountsInvitationRow.getInviter();
                }
                invitationsManager.update(request.getCode(), request.getOrgs(), inviter);
                builder.setStatus(STATUS.OK_VALUE);
            }else{
                builder.setStatus(STATUS.INVALID_PARAMETER_VALUE);
            }

        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateInvitationByEmail(InvitationUpdateRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder builder = BaseResponse.newBuilder();
        builder.setVer(1);
        if (StringUtils.isNotBlank(request.getEmail())){
            invitationsManager.updateByEmail(System.currentTimeMillis(), request.getEmail(), request.getCode());
            builder.setStatus(STATUS.OK_VALUE);
        } else {
            builder.setStatus(STATUS.INVALID_PARAMETER_VALUE);
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
}
