package org.whispersystems.textsecuregcm.controllers;

import com.codahale.metrics.annotation.Timed;
import com.github.difftim.base.respone.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.storage.AccountsManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Path("/v1/remind")
public class MessageRemindController {

  private final Logger logger = LoggerFactory.getLogger(MessageRemindController.class);
  private final AccountsManager accountsManager;


  public MessageRemindController(AccountsManager accountsManager) {
    this.accountsManager=accountsManager;
  }

  @Timed
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{number}")
  public BaseResponse unsubscribeMsgRemind (@PathParam("number")  String number) {
    if(number!=null){
      if(accountsManager.setNotRemindForMessage(number)) return BaseResponse.ok();
    }
    return BaseResponse.err(BaseResponse.STATUS.OTHER_ERROR,"operate failed!",logger);
  }
}
