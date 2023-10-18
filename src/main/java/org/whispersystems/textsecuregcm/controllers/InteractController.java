package org.whispersystems.textsecuregcm.controllers;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.dropwizard.auth.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.entities.*;
import org.whispersystems.textsecuregcm.storage.*;
import org.whispersystems.textsecuregcm.util.StringUtil;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/v1/interacts")
public class InteractController {

  private final Logger logger = LoggerFactory.getLogger(InteractController.class);

  private final InteractManager interactManager;
  private final AccountsManager accountsManager;


  public InteractController(InteractManager interactManager,AccountsManager accountsManager) {
    this.interactManager = interactManager;
    this.accountsManager=accountsManager;
  }

  @Timed
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/thumbsUp")
  public BaseResponse giveInteract(@Auth Account account, @Valid GiveInteractRequest request) {
    JsonObject jsonObject=new JsonObject();
    jsonObject.addProperty("action","giveInteract");
    jsonObject.addProperty("operator",account.getNumber());
    jsonObject.addProperty("operatorDevice",account.getAuthenticatedDevice().get().getId());
    JsonObject msgObj=new JsonObject();
    jsonObject.add("message",msgObj);
    msgObj.add("request",new Gson().toJsonTree(request));
    JsonObject responseObj=new JsonObject();
    if(request==null||StringUtil.isEmpty(request.getNumber())){
      error(jsonObject,responseObj,msgObj,Response.Status.BAD_REQUEST.getStatusCode(),BaseResponse.STATUS.INVALID_PARAMETER,"Invalid param");
    }
    Optional<Account> accountOptional=accountsManager.get(request.getNumber());
    if(!accountOptional.isPresent()){
      error(jsonObject,responseObj,msgObj,Response.Status.BAD_REQUEST.getStatusCode(),BaseResponse.STATUS.NO_SUCH_USER,"no such user");
    }
    if(accountOptional.get().isinValid()){
      error(jsonObject,responseObj,msgObj,Response.Status.BAD_REQUEST.getStatusCode(),BaseResponse.STATUS.USER_IS_DISABLED,"user is invalid");
    }
    InteractMem interactMem=null;
    try {
      Interact interact=new Interact(request.getNumber(),account.getNumber(),System.currentTimeMillis(),InteractsTable.TYPE.THUMBS_UP.getCode(), null);
      interactMem=interactManager.store(interact);
      if(interactMem==null){
        error(jsonObject,responseObj,msgObj,Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),BaseResponse.STATUS.OTHER_ERROR,"interactMem is null");
      }
    }catch (Exception e) {;
      error(jsonObject,responseObj,msgObj,Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),BaseResponse.STATUS.OTHER_ERROR,e.getMessage());
    }
    BaseResponse baseResponse=BaseResponse.ok(new GiveInteractResponse(interactMem.getThumbsUpCount(),interactMem.getLastSource()));
    msgObj.add("request",new Gson().toJsonTree(request));
    msgObj.add("response",new Gson().toJsonTree(baseResponse));
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

}
