package org.whispersystems.textsecuregcm.controllers;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.dropwizard.auth.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.configuration.ConversationConfiguration;
import org.whispersystems.textsecuregcm.entities.*;
import org.whispersystems.textsecuregcm.rpcclient.FriendGRPC;
import org.whispersystems.textsecuregcm.storage.*;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/v1/conversation")
public class ConversationController {


  private final Logger logger = LoggerFactory.getLogger(ConversationController.class);

  private final ConversationManager conversationManager;
  private final ConversationConfiguration conversationConfiguration;

  private final FriendGRPC friendGRPC;


  public ConversationController(ConversationManager conversationManager, ConversationConfiguration conversationConfiguration, FriendGRPC friendGRPC) {
    this.conversationManager = conversationManager;
    this.conversationConfiguration=conversationConfiguration;
    this.friendGRPC = friendGRPC;
  }

  @Timed
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/set")
  public BaseResponse setConversation(@Auth Account account, @Valid SetConversationRequest request) {
    JsonObject jsonObject=new JsonObject();
    jsonObject.addProperty("action","setConversation");
    jsonObject.addProperty("operator",account.getNumber());
    jsonObject.addProperty("operatorDevice",account.getAuthenticatedDevice().get().getId());
    JsonObject msgObj=new JsonObject();
    jsonObject.add("message",msgObj);
    msgObj.add("request",new Gson().toJsonTree(request));
    JsonObject responseObj=new JsonObject();
    if(!request.isValid(conversationConfiguration)){
      error(jsonObject,responseObj,msgObj,Response.Status.BAD_REQUEST.getStatusCode(),BaseResponse.STATUS.INVALID_PARAMETER,"Invalid param ");
    }
    Conversation conversation = null;
    ConversationNotify.ChangeType changeType=null;
    boolean isChange=false;
    try {
      conversation=conversationManager.get(account.getNumber(),request.getConversation());
      if(request.getMuteStatus()!=null&&(conversation==null||!request.getMuteStatus().equals(conversation.getMuteStatus()))){
        changeType=ConversationNotify.ChangeType.MUTE;
      }else if(request.getBlockStatus()!=null&&(conversation==null||!request.getBlockStatus().equals(conversation.getBlockStatus()))){
        changeType=ConversationNotify.ChangeType.BLOCK;
      }else if(request.getConfidentialMode()!=null&&(conversation==null||!request.getConfidentialMode().equals(conversation.getConfidentialMode()))){
        changeType=ConversationNotify.ChangeType.CONFIDENTIAL_MODE;
      }else if(request.getRemark()!=null&&(conversation==null||!request.getRemark().equals(conversation.getRemark()))){
        changeType=ConversationNotify.ChangeType.REMARK;
      }
      if(conversation==null){
        conversation=new Conversation(account.getNumber(), request.getConversation(),request.getRemark(),System.currentTimeMillis(),request.getMuteStatus(),
                null,null,request.getBlockStatus(), request.getConfidentialMode(),1);
        conversation=conversationManager.store(conversation);
        isChange=true;
      }else {
        if(request.getMuteStatus()!=null&&!request.getMuteStatus().equals(conversation.getMuteStatus())) {
          conversation.setMuteStatus(request.getMuteStatus());
          isChange=true;
        }
        if(request.getConfidentialMode()!=null&&!request.getConfidentialMode().equals(conversation.getConfidentialMode())) {
          conversation.setConfidentialMode(request.getConfidentialMode());
          isChange=true;
        }
        if(request.getBlockStatus()!=null&&!request.getBlockStatus().equals(conversation.getBlockStatus())) {
          conversation.setBlockStatus(request.getBlockStatus());
          isChange=true;
        }
        if(request.getRemark()!=null&&!request.getRemark().equals(conversation.getRemark())) {
          conversation.setRemark(request.getRemark());
          isChange=true;
        }
        if(isChange) {
          conversation = conversationManager.update(conversation,changeType);
        }
      }
    }catch (Exception e) {
      error(jsonObject,responseObj,msgObj,Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),BaseResponse.STATUS.OTHER_ERROR,e.getMessage());
    }
    if(isChange) {
      conversationManager.sendConversationNotify(changeType.ordinal(), account, conversation);
    }
    BaseResponse baseResponse=BaseResponse.ok(new SetConversationResponse(conversation.getConversation(),conversation.getRemark(), conversation.getMuteStatus(),
            conversation.getBlockStatus(), conversation.getConfidentialMode(), conversation.getVersion()));
    msgObj.add("request",new Gson().toJsonTree(request));
    msgObj.add("response",new Gson().toJsonTree(baseResponse));
    msgObj.addProperty("isChange",isChange);
    logger.info(jsonObject.toString());
    return baseResponse;
  }

  private void error(JsonObject jsonObject, JsonObject responseObj, JsonObject msgObj, int httpCode, BaseResponse.STATUS resultCode, String description){

    responseObj.addProperty("httpCode",httpCode);
    responseObj.addProperty("resultCode",resultCode.getState());
    responseObj.addProperty("description",description);
    msgObj.add("response",responseObj);
    logger.info(jsonObject.toString());
    BaseResponse.err(httpCode,resultCode, description,logger,null);
  }

  @Timed
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/get")
  public BaseResponse get(@Auth Account account,
                          @HeaderParam("Accept-Language") String lang,
                          @Valid GetConversationRequest request) {
    List<Conversation> conversations=conversationManager.get(account.getNumber(),request==null?null:request.getConversations());
    List<SetConversationResponse> conversationResponses=new ArrayList<>();
    if(conversations!=null) {
      List<String> conversationIds = new ArrayList<>();
      for (Conversation conversation : conversations) {
        conversationResponses.add(new SetConversationResponse(conversation.getConversation(), conversation.getRemark(), conversation.getMuteStatus(),
                conversation.getBlockStatus(), conversation.getConfidentialMode(), conversation.getVersion()));
        conversationIds.add(conversation.getConversation());
      }
     if (request != null && request.getConversations() != null && request.getConversations().size() > 0) {
        if (lang == null || lang.isEmpty()) {
          lang = "en";
        }
        lang = lang.replace('_','-');
       final FriendGRPC.SourceDescribeResult sourceDescribe = friendGRPC.getSourceDescribe(conversationIds, account.getNumber(), lang,
               request.getSourceQueryType());
       for (int i = 0; i < conversationResponses.size(); i++) {
          conversationResponses.get(i).setSourceDescribe(sourceDescribe.getDescribeList().get(i));
          conversationResponses.get(i).setFindyouDescribe(sourceDescribe.getFindyouList().get(i));
        }
      }
    }
    return BaseResponse.ok(new GetConversationResponse(conversationResponses));
  }

}
