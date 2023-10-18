package com.github.difftim.security.signing;

public class Signature {
    private long timestamp;
    private String nonce;
    private String algorithm;
    private String signature;

    public Signature(long timestamp, String nonce, String algorithm, String signature) {
        this.timestamp = timestamp;
        this.nonce = nonce;
        this.algorithm = algorithm;
        this.signature = signature;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getNonce() {
        return nonce;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getSignature() {
        return signature;
    }
}