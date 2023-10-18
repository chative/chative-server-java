package org.whispersystems.textsecuregcm.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.entities.BaseResponse;
import org.whispersystems.textsecuregcm.entities.ResponseHeader;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.*;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Locale;

@Priority(Priorities.AUTHENTICATION - 1)
@Provider
@PreMatching
public class ResponseHeaderFilter implements ContainerResponseFilter {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHeaderFilter.class);

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
        Object obj=containerResponseContext.getEntity();
        if(obj instanceof BaseResponse) {
            containerResponseContext.getHeaders().add(ResponseHeader.STATUS.name().toLowerCase(Locale.ROOT), ((BaseResponse) obj).getStatus());
            containerResponseContext.getHeaders().add(ResponseHeader.REASON.name().toLowerCase(Locale.ROOT), ((BaseResponse) obj).getReason());

        }
        if(obj instanceof com.github.difftim.base.respone.BaseResponse){
            containerResponseContext.getHeaders().add(ResponseHeader.STATUS.name().toLowerCase(Locale.ROOT), ((com.github.difftim.base.respone.BaseResponse) obj).getStatus());
            containerResponseContext.getHeaders().add(ResponseHeader.REASON.name().toLowerCase(Locale.ROOT), ((com.github.difftim.base.respone.BaseResponse) obj).getReason());
        }
    }
}
