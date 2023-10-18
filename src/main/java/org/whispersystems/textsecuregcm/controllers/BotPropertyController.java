package org.whispersystems.textsecuregcm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.entities.BaseResponse;
import org.whispersystems.textsecuregcm.storage.BotPropertyRow;
import org.whispersystems.textsecuregcm.storage.BotPropertyTable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/v1/bot_property")
@Produces(MediaType.APPLICATION_JSON)
public  class BotPropertyController {
    private final Logger logger = LoggerFactory.getLogger(BotPropertyController.class);

    BotPropertyTable botPropertyTable;

    public BotPropertyController(BotPropertyTable botPropertyTable) {
        this.botPropertyTable = botPropertyTable;
    }

    @GET
    @Path("/{number}")
    public BaseResponse get(@PathParam("number") String number) {
        final BotPropertyRow botPropertyRow = botPropertyTable.get(number);
        if (botPropertyRow == null)
            BaseResponse.err(BaseResponse.STATUS.OTHER_ERROR, "no info for this bot", logger);
        return BaseResponse.ok(botPropertyRow);
    }

}
