package com.github.difftim.security.signing.http;

import com.github.difftim.security.signing.Signature;

public class HttpSignature extends Signature {

    private String headers;

    public HttpSignature(long timestamp, String nonce, String algorithm, String signature, String headers) {
        super(timestamp, nonce, algorithm, signature);
        this.headers = headers;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }
}
