package com.github.difftim.security.signing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Signer {

    public final String ALGORITHM_HMAC_SHA256 = "HmacSHA256";

    private final Logger logger = LoggerFactory.getLogger(SignatureVerifier.class);
    final private String algorithm = ALGORITHM_HMAC_SHA256;

    final private String appid;
    final private byte[] key;

    public Signer(String appid, byte[] key) {
        this.appid = appid;
        this.key = key;
    }

    public Signature sign(byte[] data)
            throws SignatureVerifier.ServerErrorException {

        // timestamp
        long timestamp = System.currentTimeMillis();
        String nonce = Utils.getNonce();

        // data to sign
        byte[] dataToSign;
        try {
            dataToSign = Utils.getPreSignedData(appid, timestamp, nonce, data);
        } catch (IOException e) {
            logger.error("failed to get pre-signed data", e);
            throw new SignatureVerifier.ServerErrorException();
        }

        // sign
        try {
            String signature = Utils.sign(algorithm, key, dataToSign);
            return new Signature(timestamp, nonce, algorithm, signature);
        } catch (NoSuchAlgorithmException e) {
            logger.error("no such algorithm: " + algorithm, e);
            throw new SignatureVerifier.ServerErrorException("no such algorithm: " + algorithm);
        } catch (InvalidKeyException e) {
            logger.error("invalid key: " + key, e);
            throw new SignatureVerifier.ServerErrorException("invalid key: " + key);
        }
    }
}
