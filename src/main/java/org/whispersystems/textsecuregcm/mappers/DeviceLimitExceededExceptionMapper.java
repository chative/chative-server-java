package org.whispersystems.textsecuregcm.mappers;


import com.fasterxml.jackson.annotation.JsonProperty;

import org.whispersystems.textsecuregcm.controllers.DeviceLimitExceededException;
import org.whispersystems.textsecuregcm.eslogger.ExceptionLog;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DeviceLimitExceededExceptionMapper implements ExceptionMapper<DeviceLimitExceededException> {
  @Override
  public Response toResponse(DeviceLimitExceededException exception) {
    ExceptionLog.exception(exception,411);
    return Response.status(411)
                   .entity(new DeviceLimitExceededDetails(exception.getCurrentDevices(),
                                                          exception.getMaxDevices()))
                   .build();
  }

  private static class DeviceLimitExceededDetails {
    @JsonProperty
    private int current;
    @JsonProperty
    private int max;

    public DeviceLimitExceededDetails(int current, int max) {
      this.current = current;
      this.max     = max;
    }
  }
}
