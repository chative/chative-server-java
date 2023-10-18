package org.whispersystems.textsecuregcm.mappers;

import org.whispersystems.textsecuregcm.eslogger.ExceptionLog;
import org.whispersystems.textsecuregcm.exceptions.RegisterException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RegisterExceptionMapper implements ExceptionMapper<RegisterException> {

//    private final Logger logger = LoggerFactory.getLogger(RegisterExceptionMapper.class);

    @Override
    public Response toResponse(RegisterException e) {
        ExceptionLog.exception(e, 403);
        return Response.status(403).entity(e.getReason()).build();
    }
}
