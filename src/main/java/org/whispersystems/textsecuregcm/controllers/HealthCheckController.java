package org.whispersystems.textsecuregcm.controllers;

import com.codahale.metrics.annotation.Timed;
import org.whispersystems.textsecuregcm.entities.BaseResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/v1/health")
public class HealthCheckController {

    @Timed
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public BaseResponse healthCheck() {
        return BaseResponse.ok();
    }
}
