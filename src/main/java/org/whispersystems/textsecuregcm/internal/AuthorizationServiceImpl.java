package org.whispersystems.textsecuregcm.internal;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.protobuf.Any;
import io.grpc.stub.StreamObserver;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.controllers.RateLimitExceededException;
import org.whispersystems.textsecuregcm.internal.auth.AuthorizationCreateTokenResponse;
import org.whispersystems.textsecuregcm.internal.auth.AuthorizationServiceGrpc;
import org.whispersystems.textsecuregcm.internal.auth.AuthorizationVerifyTokenRequest;
import org.whispersystems.textsecuregcm.internal.common.BaseResponse;
import org.whispersystems.textsecuregcm.internal.common.Empty;
import org.whispersystems.textsecuregcm.internal.common.STATUS;
import org.whispersystems.textsecuregcm.limits.RateLimiters;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.AccountsManager;
import org.whispersystems.textsecuregcm.storage.Device;

import java.io.IOException;
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

public class AuthorizationServiceImpl extends AuthorizationServiceGrpc.AuthorizationServiceImplBase{
    final private Logger logger = LoggerFactory.getLogger(AuthorizationServiceImpl.class);

    private final AccountsManager accountsManager;
    private final RateLimiters rateLimiters;
    private final ECPublicKey publicKey;
    private final ECPrivateKey privateKey;
    private final Algorithm algorithm;


    public AuthorizationServiceImpl(AccountsManager accountsManager, RateLimiters rateLimiters, String publicKey,
                                    String privateKey) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        this.accountsManager = accountsManager;

        this.rateLimiters = rateLimiters;

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
    }

    @Override
    public void createToken(Empty request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder responseBuilder = BaseResponse.newBuilder();
        AuthorizationCreateTokenResponse.Builder createTokenResponseBuilder=AuthorizationCreateTokenResponse.newBuilder();
        Account account=UserAuthServerInsterceptor.getCurrentContextAccount();

        responseBuilder.setVer(1);
        String token= null;
        try {
            rateLimiters.getAuthorizationLimiter().validate(account.getNumber());
            HashMap<String, Object> payload = new HashMap(){{
                put("ver", 1);
                put("uid", account.getNumber());
                put("did", account.getAuthenticatedDevice().get().getId());
            }};

            token = JWT.create()
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24)))
                    .withPayload(payload)
                    .sign(algorithm);
            responseBuilder.setStatus(STATUS.OK_VALUE);
            responseBuilder.setReason("OK");
            responseBuilder.setData(Any.pack(createTokenResponseBuilder.setToken(token).build()));
        } catch (JWTCreationException e) {
            responseBuilder.setStatus(STATUS.SERVER_INTERNAL_ERROR_VALUE);
            responseBuilder.setReason(e.getMessage());
        } catch (RateLimitExceededException e) {
            responseBuilder.setStatus(STATUS.OTHER_ERROR_VALUE);
            responseBuilder.setReason(e.getMessage());
        }
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void verifyToken(AuthorizationVerifyTokenRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder responseBuilder = BaseResponse.newBuilder();
        try {
            responseBuilder.setVer(1);
            responseBuilder.setStatus(STATUS.OK_VALUE);
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
                    responseBuilder.setStatus(STATUS.INVALID_TOKEN_VALUE);
                    responseBuilder.setReason("Invalid token version: " + request.getToken());
            }
            // account exists?
            Optional<Account> account =  accountsManager.get(uid);
            if (!account.isPresent()) {
                responseBuilder.setStatus(STATUS.INVALID_TOKEN_VALUE);
                responseBuilder.setReason("No such user: " + uid);
            }else {
                // account is not disabled?
                if (account.get().isinValid()) {
                    responseBuilder.setStatus(STATUS.INVALID_TOKEN_VALUE);
                    responseBuilder.setReason("The account is disabled: " + uid);
                }

                // device exists?
                Optional<Device> device = account.get().getDevice(did);
                if (!device.isPresent()) {
                    responseBuilder.setStatus(STATUS.INVALID_TOKEN_VALUE);
                    responseBuilder.setReason("No such device: " + did);
                }
            }

        } catch (JWTVerificationException e) {
            responseBuilder.setStatus(STATUS.INVALID_TOKEN_VALUE);
            responseBuilder.setReason("Invalid token: " + request.getToken());
        }
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
