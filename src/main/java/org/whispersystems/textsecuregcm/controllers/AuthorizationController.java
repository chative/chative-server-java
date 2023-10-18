// package org.whispersystems.textsecuregcm.controllers;
//
// import com.auth0.jwt.JWT;
// import com.auth0.jwt.JWTVerifier;
// import com.auth0.jwt.algorithms.Algorithm;
// import com.auth0.jwt.exceptions.JWTCreationException;
// import com.auth0.jwt.exceptions.JWTVerificationException;
// import com.auth0.jwt.interfaces.DecodedJWT;
// import com.codahale.metrics.annotation.Timed;
// import io.dropwizard.auth.Auth;
// import org.bouncycastle.util.io.pem.PemObject;
// import org.bouncycastle.util.io.pem.PemReader;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.whispersystems.textsecuregcm.entities.*;
// import org.whispersystems.textsecuregcm.limits.RateLimiters;
// import org.whispersystems.textsecuregcm.storage.Account;
// import org.whispersystems.textsecuregcm.storage.AccountsManager;
// import org.whispersystems.textsecuregcm.storage.Device;
//
// import javax.validation.Valid;
// import javax.ws.rs.*;
// import javax.ws.rs.core.MediaType;
// import java.io.*;
// import java.security.*;
// import java.security.interfaces.ECPrivateKey;
// import java.security.interfaces.ECPublicKey;
// import java.security.spec.InvalidKeySpecException;
// import java.security.spec.PKCS8EncodedKeySpec;
// import java.security.spec.X509EncodedKeySpec;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.Optional;
//
// @Path("/v1/authorize")
// public class AuthorizationController {
//   private final Logger logger = LoggerFactory.getLogger(AccountController.class);
//
//   private final RateLimiters rateLimiters;
//
//   private final ECPublicKey publicKey;
//   private final ECPrivateKey privateKey;
//   private final Algorithm algorithm;
//
//   private final AccountsManager accountsManager;
//
//   public AuthorizationController(AccountsManager accountsManager, RateLimiters rateLimiters, String publicKey,
//                                  String privateKey)
//           throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
//     this.accountsManager = accountsManager;
//
//     this.rateLimiters = rateLimiters;
//
//     try (PemReader pemReader = new PemReader(new StringReader(publicKey))) {
//       PemObject pemObject = pemReader.readPemObject();
//       byte[] content = pemObject.getContent();
//       KeyFactory factory = KeyFactory.getInstance("EC");
//       this.publicKey = (ECPublicKey) factory.generatePublic(new X509EncodedKeySpec(content));
//     }
//
//     try (PemReader pemReader = new PemReader(new StringReader(privateKey))) {
//       PemObject pemObject = pemReader.readPemObject();
//       byte[] content = pemObject.getContent();
//       KeyFactory factory = KeyFactory.getInstance("EC");
//       this.privateKey = (ECPrivateKey) factory.generatePrivate(new PKCS8EncodedKeySpec(content));
//     }
//
//     this.algorithm = Algorithm.ECDSA512(this.publicKey, this.privateKey);
//   }
//
//   @Timed
//   @PUT
//   @Produces(MediaType.APPLICATION_JSON)
//   @Path("/token")
//   public BaseResponse createToken(@Auth Account account) throws RateLimitExceededException {
//
//     rateLimiters.getAuthorizationLimiter().validate(account.getNumber());
//
//     String token= null;
//     try {
//       HashMap<String, Object> payload = new HashMap(){{
//         put("ver", 1);
//         put("uid", account.getNumber());
//         put("did", account.getAuthenticatedDevice().get().getId());
//       }};
//
//       token = JWT.create()
//           .withIssuedAt(new Date())
//           .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * 60 * 20)))
//           .withPayload(payload)
//           .sign(algorithm);
//     } catch (JWTCreationException e) {
//       BaseResponse.err(BaseResponse.STATUS.SERVER_INTERNAL_ERROR, e.getMessage(), logger);
//     }
//
//     return BaseResponse.ok(new AuthorizationCreateTokenResponse(token), logger);
//   }
//
//   @Timed
//   @POST
//   @Consumes(MediaType.APPLICATION_JSON)
//   @Produces(MediaType.APPLICATION_JSON)
//   @Path("/token/verify")
//   public BaseResponse verifyToken(@Valid AuthorizationVerifyTokenRequest request) {
//
//     try {
//       JWTVerifier verifier = JWT.require(algorithm)
//           .build();
//       DecodedJWT jwt = verifier.verify(request.getToken());
//       int ver = jwt.getClaim("ver").asInt();
//       String uid = jwt.getClaim("uid").asString();
//       long did = jwt.getClaim("did").asLong();
//
//       // token version
//       switch (ver) {
//         case 1:
//           break;
//         default:
//           BaseResponse.err(BaseResponse.STATUS.INVALID_TOKEN, "Invalid token version: " + request.getToken(), logger);
//       }
//
//       // account exists?
//       Optional<Account> account =  accountsManager.get(uid);
//       if (!account.isPresent()) {
//         BaseResponse.err(BaseResponse.STATUS.INVALID_TOKEN, "No such user: " + uid, logger);
//       }
//
//       // account is not disabled?
//       if (account.get().isDisabled()) {
//         BaseResponse.err(BaseResponse.STATUS.INVALID_TOKEN, "The account is disabled: " + uid, logger);
//       }
//
//       // device exists?
//       Optional<Device> device = account.get().getDevice(did);
//       if (!device.isPresent()) {
//         BaseResponse.err(BaseResponse.STATUS.INVALID_TOKEN, "No such device: " + did, logger);
//       }
//
//     } catch (JWTVerificationException e) {
//       BaseResponse.err(BaseResponse.STATUS.INVALID_TOKEN, "Invalid token: " + request.getToken(), logger);
//     }
//
//     return BaseResponse.ok();
//   }
// }


package org.whispersystems.textsecuregcm.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.dropwizard.auth.Auth;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.entities.AuthorizationCreateTokenResponse;
import org.whispersystems.textsecuregcm.entities.AuthorizationVerifyTokenRequest;
import org.whispersystems.textsecuregcm.entities.BaseResponse;
import org.whispersystems.textsecuregcm.limits.RateLimiters;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.AccountsManager;
import org.whispersystems.textsecuregcm.storage.Device;
import org.whispersystems.textsecuregcm.util.ParameterValidator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

@Path("/v1/authorize")
public class AuthorizationController {
  private final Logger logger = LoggerFactory.getLogger(AccountController.class);

  private final RateLimiters rateLimiters;
  final private String meetingTokenSecret;

  private final ECPublicKey publicKey;
  private final ECPrivateKey privateKey;
  private final Algorithm algorithm;

  private final AccountsManager accountsManager;
  private final List<String> containScopes = new ArrayList<>();
  private long tokenWithAppIdValidDuration = 1000 * 60 * 60 * 1L;
  private long tokenDefaultValidDuration = 1000 * 60 * 60 * 24L;

  public AuthorizationController(AccountsManager accountsManager, RateLimiters rateLimiters, String publicKey,
                                 String privateKey, String meetingTokenSecret)
          throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    this.accountsManager = accountsManager;
    this.rateLimiters = rateLimiters;
    this.meetingTokenSecret = meetingTokenSecret;

    try (PemReader pemReader = new PemReader(new StringReader(publicKey))) {
      PemObject pemObject = pemReader.readPemObject();
      byte[] content = pemObject.getContent();
      KeyFactory factory = KeyFactory.getInstance("EC");
      this.publicKey = (ECPublicKey) factory.generatePublic(new X509EncodedKeySpec(content));
    }

    try (PemReader pemReader = new PemReader(new StringReader(privateKey))) {
      PemObject pemObject = pemReader.readPemObject();
      byte[] content = pemObject.getContent();
      KeyFactory factory = KeyFactory.getInstance("EC");
      this.privateKey = (ECPrivateKey) factory.generatePrivate(new PKCS8EncodedKeySpec(content));
    }

    this.algorithm = Algorithm.ECDSA512(this.publicKey, this.privateKey);

    containScopes.add("NameRead");
    containScopes.add("EmailRead");
  }

  @Timed
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/token")
  public BaseResponse createToken(@Auth Account account,
                                  @Context HttpServletRequest request) throws RateLimitExceededException {

    rateLimiters.getAuthorizationLimiter().validate(account.getNumber());

    String token= null;
    try {
      HashMap<String, Object> payload = new HashMap(){{
        put("ver", 1);
        put("uid", account.getNumber());
        put("did", account.getAuthenticatedDevice().get().getId());
      }};
      String appid = null;
      String scope = null;
      appid = request.getParameter("appid");
      scope = request.getParameter("scope");

      if (appid == null || scope == null) {
        BufferedReader streamReader = new BufferedReader( new InputStreamReader(request.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String inputStr;
        while ((inputStr = streamReader.readLine()) != null) {
          sb.append(inputStr);
        }
        try {
          if (!"".equals(sb.toString())) {
            JsonObject mapObj = new JsonParser().parse(sb.toString()).getAsJsonObject();
            appid = mapObj.get("appid").getAsString();
            scope = mapObj.get("scope").getAsString();
          }
        } catch (Exception e) {
          // if error,do nothing; can continue
        }
      }
      long tokenDuration = this.tokenDefaultValidDuration;
      if (appid != null) {
        if (!ParameterValidator.validateOauthAppClientId(appid)) {
          throw new WebApplicationException(Response.status(400).build());
        }
        tokenDuration = this.tokenWithAppIdValidDuration;
        payload.put("appid", appid);
      }
      if (scope != null) {
        if (!ParameterValidator.validateOauthScope(scope)) {
          throw new WebApplicationException(Response.status(400).build());
        }
        List<String> temp = new ArrayList<>();
        String[] scopeArr = scope.split(",");
        for (String s : scopeArr) {
          if (!containScopes.contains(s)) {
            throw new WebApplicationException(Response.status(400).build());
          } else {
            temp.add(s);
          }
        }
        payload.put("scope", temp);
      }
      token = JWT.create()
          .withIssuedAt(new Date())
          .withExpiresAt(new Date(System.currentTimeMillis() + tokenDuration))
          .withPayload(payload)
          .sign(algorithm);
    } catch (JWTCreationException | IOException e) {
      BaseResponse.err(BaseResponse.STATUS.SERVER_INTERNAL_ERROR, e.getMessage(), logger);
    }

    return BaseResponse.ok(new AuthorizationCreateTokenResponse(token), logger);
  }

  @Timed
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/token/verify")
  public BaseResponse verifyToken(@Valid AuthorizationVerifyTokenRequest request) {

    try {
      JWTVerifier verifier = JWT.require(algorithm)
              .build();
      DecodedJWT jwt = verifier.verify(request.getToken());
      int ver = jwt.getClaim("ver").asInt();
      String uid = jwt.getClaim("uid").asString();
      long did = jwt.getClaim("did").asLong();

      // token version
      switch (ver) {
        case 1:
          break;
        default:
          BaseResponse.err(BaseResponse.STATUS.INVALID_TOKEN, "Invalid token version: " + request.getToken(), logger);
      }

      // account exists?
      Optional<Account> account =  accountsManager.get(uid);
      if (!account.isPresent()) {
        BaseResponse.err(BaseResponse.STATUS.INVALID_TOKEN, "No such user: " + uid, logger);
      }

      // account is not disabled?
      if (account.get().isinValid()) {
        BaseResponse.err(BaseResponse.STATUS.INVALID_TOKEN, "The account is disabled: " + uid, logger);
      }

      // device exists?
      Optional<Device> device = account.get().getDevice(did);
      if (!device.isPresent()) {
        BaseResponse.err(BaseResponse.STATUS.INVALID_TOKEN, "No such device: " + did, logger);
      }

      // check Teams
      final Set<String> mustTeams = request.getMustTeams();
      if (mustTeams != null && !mustTeams.isEmpty())
        if (!mustTeams.removeAll(accountsManager.getUserTeams(uid)) || !mustTeams.isEmpty())
          BaseResponse.err(BaseResponse.STATUS.INVALID_TOKEN, "No such team: " + mustTeams, logger);

    } catch (JWTVerificationException e) {
      try {
        // legacy meeting token
        // verify
        Algorithm algorithm = Algorithm.HMAC512(meetingTokenSecret);
        JWTVerifier verifier = JWT.require(algorithm)
                .build();
        DecodedJWT jwt = verifier.verify(request.getToken());

        // expired?
        Date expiresAt = jwt.getExpiresAt();
        if (expiresAt.before(new Date())) {
          BaseResponse.err(BaseResponse.STATUS.INVALID_TOKEN, "Token has expired: " + request.getToken(), logger);
        }
      } catch (JWTVerificationException e2) {
        BaseResponse.err(BaseResponse.STATUS.INVALID_TOKEN, "Invalid token: " + request.getToken(), logger);
      }
      return BaseResponse.ok();
      // BaseResponse.err(BaseResponse.STATUS.INVALID_TOKEN, "Invalid token: " + request.getToken(), logger);
    }

    return BaseResponse.ok();
  }
}
