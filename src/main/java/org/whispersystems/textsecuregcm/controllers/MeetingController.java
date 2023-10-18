package org.whispersystems.textsecuregcm.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.entities.BaseResponse;
import org.whispersystems.textsecuregcm.entities.MeetingCreateTokenResponse;
import org.whispersystems.textsecuregcm.entities.MeetingVerifyTokenRequest;
import org.whispersystems.textsecuregcm.storage.Account;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.HashMap;

// TODO: deprecated, remove this controller, use AuthorizationController instead.
@Deprecated
@Path("/v1/meeting")
public class MeetingController {
  private final Logger logger = LoggerFactory.getLogger(AccountController.class);
  private final String tokenSecret;

  public MeetingController(String tokenSecret) {
    this.tokenSecret = tokenSecret;
  }

  @Timed
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/token")
  public BaseResponse createToken(@Auth Account account) {
    String token= null;
    try {
      HashMap<String, Object> payload = new HashMap();
      payload.put("ver", 1);
      long iat = System.currentTimeMillis() / 1000;
      payload.put("iat", iat);
      payload.put("exp", iat + (60 * 20)); // 20m
      payload.put("uid", account.getNumber());

      Algorithm algorithm = Algorithm.HMAC512(tokenSecret);
      token = JWT.create()
          .withPayload(payload)
          .sign(algorithm);
    } catch (JWTCreationException e) {
      BaseResponse.err(BaseResponse.STATUS.SERVER_INTERNAL_ERROR, e.getMessage(), logger);
    }
    return BaseResponse.ok(new MeetingCreateTokenResponse(token), logger);
  }

  @Timed
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/token/verify")
  public BaseResponse verifyToken(@Valid MeetingVerifyTokenRequest request) {
    try {
      // verify
      Algorithm algorithm = Algorithm.HMAC512(tokenSecret);
      JWTVerifier verifier = JWT.require(algorithm)
          .build();
      DecodedJWT jwt = verifier.verify(request.getToken());

      // expired?
      Date expiresAt = jwt.getExpiresAt();
      if (expiresAt.before(new Date())) {
        BaseResponse.err(BaseResponse.STATUS.INVALID_TOKEN, "Token has expired: " + request.getToken(), logger);
      }
    } catch (JWTVerificationException e) {
      BaseResponse.err(BaseResponse.STATUS.INVALID_TOKEN, "Invalid token: " + request.getToken(), logger);
    }
    return BaseResponse.ok();
  }
}
