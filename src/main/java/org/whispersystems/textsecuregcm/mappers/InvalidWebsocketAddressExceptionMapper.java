package org.whispersystems.textsecuregcm.mappers;

import org.whispersystems.textsecuregcm.eslogger.ExceptionLog;
import org.whispersystems.textsecuregcm.websocket.InvalidWebsocketAddressException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidWebsocketAddressExceptionMapper implements ExceptionMapper<InvalidWebsocketAddressException> {
  @Override
  public Response toResponse(InvalidWebsocketAddressException exception) {
    ExceptionLog.exception(exception,Response.Status.BAD_REQUEST.getStatusCode());
    return Response.status(Response.Status.BAD_REQUEST).build();
  }
}
