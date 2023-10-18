package org.whispersystems.textsecuregcm.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

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
import java.util.Map;

public class JwtHelper {
    //String publicKey;
    //String privateKey;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;


    public JwtHelper(String publicKeyStr, String privateKeyStr) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        ECPublicKey publicKey;
        ECPrivateKey privateKey;
        try (PemReader pemReader = new PemReader(new StringReader(publicKeyStr))) {
            PemObject pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();
            KeyFactory factory = KeyFactory.getInstance("EC");
            publicKey = (ECPublicKey) factory.generatePublic(new X509EncodedKeySpec(content));
        }

        try (PemReader pemReader = new PemReader(new StringReader(privateKeyStr))) {
            PemObject pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();
            KeyFactory factory = KeyFactory.getInstance("EC");
            privateKey = (ECPrivateKey) factory.generatePrivate(new PKCS8EncodedKeySpec(content));
        }

        algorithm = Algorithm.ECDSA512(publicKey, privateKey);
        verifier = JWT.require(algorithm).build();
    }

    public String Sign(Map<String, Object> payload, long tokenDuration) {
        return JWT.create()
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + tokenDuration))
                .withPayload(payload)
                .sign(algorithm);

    }

    public DecodedJWT verify(String token) {
        return verifier.verify(token);
    }
}
