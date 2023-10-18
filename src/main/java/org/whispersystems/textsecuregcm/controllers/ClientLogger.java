package org.whispersystems.textsecuregcm.controllers;

import com.codahale.metrics.annotation.Timed;
import com.github.difftim.eslogger.ESLogger;
import io.dropwizard.auth.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.Device;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.Optional;

@Path("/v1/cltlog")
public class ClientLogger {

    @Timed
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public OKRes saveLog(@Context HttpServletRequest request,
                        @Auth Account account,
                        @QueryParam("level") String level,
                        Map<String, Object> jsonMap) {
        ESLogger log = new ESLogger("clientlogs");
        log.withCustom("ua", request.getHeader("User-Agent"));
        log.withCustom("level", level);
        log.withCustom("data", jsonMap).withUID(account.getNumber());
        Optional<Device> optionalDevice = account.getAuthenticatedDevice();
        optionalDevice.ifPresent(device -> log.withDeviceID(Long.toString(device.getId())));
        log.send();
        return new OKRes(0);
    }

    class OKRes {
        long status;

        public OKRes(long status) {
            this.status = status;
        }
    }
}
