package org.whispersystems.textsecuregcm.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

public class TokenUtil {
  private final Logger logger = LoggerFactory.getLogger(TokenUtil.class);

  private final ECPublicKey publicKey;
  private final ECPrivateKey privateKey;
  private long effectiveDuration;
  private final Algorithm algorithm;


  public TokenUtil(String publicKey, String privateKey,long effectiveDuration)
          throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    try (PemReader pemReader = new PemReader(new StringReader(publicKey))) {
      PemObject pemObject = pemReader.readPemObject();
      byte[] content = pemObject.getContent();
      KeyFactory factory = KeyFactory.getInstance("EC");
      this.publicKey = (ECPublicKey) factory.generatePublic(new X509EncodedKeySpec(content));
      this.effectiveDuration=effectiveDuration;
    }

    try (PemReader pemReader = new PemReader(new StringReader(privateKey))) {
      PemObject pemObject = pemReader.readPemObject();
      byte[] content = pemObject.getContent();
      KeyFactory factory = KeyFactory.getInstance("EC");
      this.privateKey = (ECPrivateKey) factory.generatePrivate(new PKCS8EncodedKeySpec(content));
    }

    this.algorithm = Algorithm.ECDSA512(this.publicKey, this.privateKey);
  }
  public String createToken(Map<String, Object> payload) {
    return createToken(payload,effectiveDuration);
  }

  public String createToken(Map<String, Object> payload, long effectiveDuration) {
    String token= null;
    try {
      token = JWT.create()
          .withIssuedAt(new Date())
          .withExpiresAt(new Date(System.currentTimeMillis() + effectiveDuration))
          .withPayload(payload)
          .sign(algorithm);
    } catch (JWTCreationException e) {
      logger.error("createToken error! msg:{}",e.getMessage());
    }
    return token;
  }

  public String createTokenNoExpire(Map<String, Object> payload) {
    String token= null;
    try {
      token = JWT.create()
              .withIssuedAt(new Date())
              .withPayload(payload)
              .sign(algorithm);
    } catch (JWTCreationException e) {
      logger.error("createToken error! msg:{}",e.getMessage());
    }
    return token;
  }


  public Map<String, Claim>  verifyToken(String token) {
    Map<String, Claim> payload=null;
    try {
      JWTVerifier verifier = JWT.require(algorithm)
          .build();
      DecodedJWT jwt = verifier.verify(token);
      Date expiresAt = jwt.getExpiresAt();
      if (expiresAt.before(new Date())) {
        logger.error("verifyToken error! token is expired:{}",token);
        return null;
      }
      payload=jwt.getClaims();
    } catch (JWTVerificationException e) {
      logger.error("verifyToken error! msg:{}",e.getMessage());
    }
    return payload;
  }

  public Map<String, Claim>  verifyToken(String token,long effectiveDuration ) {
    Map<String, Claim> payload=null;
    try {
      JWTVerifier verifier = JWT.require(algorithm)
              .build();
      DecodedJWT jwt = verifier.verify(token);
      if ( jwt.getIssuedAt().getTime() + effectiveDuration < System.currentTimeMillis()) {
        logger.error("verifyToken error! token is expired:{}",token);
        return null;
      }
      payload=jwt.getClaims();
    } catch (JWTVerificationException e) {
      logger.error("verifyToken error! msg:{}",e.getMessage());
    }
    return payload;
  }

}
