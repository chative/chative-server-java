package org.whispersystems.textsecuregcm.controllers;

import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.limits.IPAllowList;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.AccountsManager;
import org.whispersystems.textsecuregcm.storage.Device;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/v1/internalapi")
public class InternalAPIController {
  private final Logger logger            = LoggerFactory.getLogger(InternalAPIController.class);
  private final IPAllowList ipAllowList;
  private final AccountsManager accountsManager;

  public InternalAPIController(IPAllowList ipAllowList, AccountsManager accountsManager) {
    this.ipAllowList = ipAllowList;
    this.accountsManager = accountsManager;
  }

  @Timed
  @GET
  @Path("/account/disable/{number}")
  public Response disable(@Context HttpServletRequest request,
                          @PathParam("number") String number
  )
  {
    ipAllowList.LocalOnly(request);
    // TODO: add a token for security, ip allow list is not enough due to the nginx

    logger.info("disable user: " + number);
    Optional<Account> account = accountsManager.get(number);
    if (!account.isPresent()) {
      throw new WebApplicationException(Response.status(404).build());
    }

    account.get().setDisabled(true);
    accountsManager.update(account.get(),null,false);

    return Response.ok().build();
  }

  @Timed
  @GET
  @Path("/account/kick_off_device/{number}")
  public Response kickOffDevice(@Context HttpServletRequest request,
                                @PathParam("number") String number,
                                @QueryParam("device") long deviceID
  )
  {
    // 也只做断开链接操作
    ipAllowList.LocalOnly(request);

    logger.info("kickOffDevice user: number:{},device:{}" , number,deviceID);
    Optional<Account> account = accountsManager.get(number);
    if (!account.isPresent()) {
      throw new WebApplicationException(Response.status(404).build());
    }

    if (deviceID != 0 ){
      accountsManager.kickOffDevice(number,deviceID);
    } else {
      for (Device device : account.get().getDevices()) {
        accountsManager.kickOffDevice(number,device.getId());
      }
    }

    return Response.ok().build();
  }


  @Timed
  @GET
  @Path("/account/enable/{number}")
  public Response enable(@Context HttpServletRequest request,
                         @PathParam("number") String number
  )
  {
    ipAllowList.LocalOnly(request);
    // TODO: add a token for security, ip allow list is not enough due to the nginx

    logger.info("enable user: " + number);
    Optional<Account> account = accountsManager.get(number);
    if (!account.isPresent()) {
      throw new WebApplicationException(Response.status(404).build());
    }

    account.get().setDisabled(true);
    accountsManager.update(account.get(),null,false);

    return Response.ok().build();
  }

  @Timed
  @GET
  @Path("/account/clearcache/{number}")
  public Response clearCache(@Context HttpServletRequest request,
                             @PathParam("number")    String number
  )
  {
    ipAllowList.LocalOnly(request);
    // TODO: add a token for security, ip allow list is not enough due to the nginx

    if (!accountsManager.reload(number).isPresent()) {
      return Response.status(404).build();
    }
    return Response.ok().build();
  }
}
