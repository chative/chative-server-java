package org.whispersystems.textsecuregcm.controllers;

import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/error")
public class ErrorController {

    public ErrorController () {

    }

    @Path("404")
    @GET
    @Timed
    public Response error(){
        return Response.status(404).build();
    }
}
