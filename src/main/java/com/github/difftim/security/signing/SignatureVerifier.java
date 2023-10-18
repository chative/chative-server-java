package com.github.difftim.security.signing;

import com.github.difftim.security.signing.http.HttpSigner;
import inet.ipaddr.IPAddressString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class SignatureVerifier {

    static public class InvalidAppIDException extends RuntimeException {
    }

    static public class ExpiredException extends RuntimeException {
    }

    static public class SourceIPNotAllowedException extends RuntimeException {
    }

    static public class InvalidSignatureException extends RuntimeException {
    }

    static public class ReplayRequestException extends RuntimeException {
    }

    static public class ServerErrorException extends RuntimeException {
        public ServerErrorException(){super();}
        public ServerErrorException(String message){super(message);}
    }

    static public class Key {
        private final String algorithm;
        private final byte[] key;
        private final int signatureExpireTime;
        private final List<String> allowedIPList;

        public Key(String algorithm, byte[] key, int signatureExpireTime, List<String> allowedIPList) {
            this.algorithm = algorithm;
            this.key = key;
            this.signatureExpireTime = signatureExpireTime;
            this.allowedIPList = allowedIPList;
        }

        public String getAlgorithm() {
            return algorithm;
        }

        public byte[] getKey() {
            return key;
        }

        public int getSignatureExpireTime() {
            return signatureExpireTime;
        }

        public List<String> getAllowedIPList() {
            return allowedIPList;
        }
    }

    public interface KeyStorage {

        Key getKey(String appid);

        void rememberNonce(String nonce, long millisecondsToExpire);

        boolean nonceExists(String nonce);
    }

    final private Logger logger = LoggerFactory.getLogger(SignatureVerifier.class);

    final private KeyStorage keyStorage;

    public SignatureVerifier(KeyStorage keyStorage) {
        this.keyStorage = keyStorage;
    }

    public void verify(String appid, long timestamp, String nonce, String signature, byte[] data, String sourceIP)
            throws InvalidAppIDException, ExpiredException, SourceIPNotAllowedException, InvalidSignatureException, ReplayRequestException, ServerErrorException {
        // appid
        Key key = keyStorage.getKey(appid);
        if (null == key) {
            logger.warn("key not found: " + appid);
            throw new InvalidAppIDException();
        }

        // timestamp
        if (System.currentTimeMillis() > timestamp + key.getSignatureExpireTime()) {
            logger.warn("signature is expired: " + timestamp);
            throw new ExpiredException();
        }

        // IP
        boolean ipAllowed = false;
        for (String ip : key.getAllowedIPList()) {
            if (new IPAddressString(ip).getAddress().contains(new IPAddressString(sourceIP).getAddress())) {
                ipAllowed = true;
                break;
            }
        }
        if (!ipAllowed) {
            logger.warn("source IP is not allowed: " + sourceIP);
            throw new SourceIPNotAllowedException();
        }

        // signature
        byte[] preSignedData;
        try {
            preSignedData = Utils.getPreSignedData(appid, timestamp, nonce, data);
        } catch (IOException e) {
            logger.error("failed to get pre-signed data", e);
            throw new ServerErrorException();
        }

        try {
            String calculatedSignature = Utils.sign(key.getAlgorithm(), key.getKey(), preSignedData);
            if (!calculatedSignature.equals(signature)) {
                logger.warn("signature is not correct: " + calculatedSignature + "!=" + signature);
                throw new InvalidSignatureException();
            }
        } catch (Exception e) {
            logger.error("failed to calculate signature", e);
            throw new InvalidSignatureException();
        }

        // nonce
        if (keyStorage.nonceExists(nonce)) {
            logger.warn("replay request detected: " + signature);
            throw new ReplayRequestException();
        }

        keyStorage.rememberNonce(nonce, key.getSignatureExpireTime() + 10000);
    }
}
