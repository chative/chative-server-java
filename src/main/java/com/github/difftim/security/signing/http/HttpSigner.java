package com.github.difftim.security.signing.http;

import com.github.difftim.security.signing.Signature;
import com.github.difftim.security.signing.Signer;
import com.github.difftim.security.utils.Utils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class HttpSigner {

    static public class InvalidHeaderKeyException extends RuntimeException {
        public InvalidHeaderKeyException(){super();}
        public InvalidHeaderKeyException(String message){super(message);}
    }

    final private String appid;
    final private byte[] key;

    final private Signer signer;

    public HttpSigner(String appid, byte[] key) {
        this.appid = appid;
        this.key = key;

        this.signer = new Signer(appid, key);
    }

    public String getAppid() {
        return appid;
    }

    public HttpSignature sign(String method, String uri, Map<String, List<String>> parameters, Map<String, String> headers, byte[] data) throws InvalidHeaderKeyException {

        headers = Utils.mapToLowerCase(headers);

        Signature signature = signer.sign(buildData(method, uri, parameters, headers, data));

        // headers
        String headerKeys = "";
        if (null != headers) {
            for (String header : headers.keySet()) {
                if (-1 != header.indexOf(",")) {
                    throw new InvalidHeaderKeyException("invalid header name: " + header);
                }
            }
            headerKeys = String.join(",", headers.keySet());
        }

        return new HttpSignature(signature.getTimestamp(), signature.getNonce(), signature.getAlgorithm(), signature.getSignature(), headerKeys);
    }

    static public byte[] buildData(String method, String uri, Map<String, List<String>> parameters, Map<String, String> headers, byte[] data) {
        // URI
        String requestToSign = method + ";" + uri + ";";

        // parameters
        TreeMap<String, List<String>> sortedParameters = new TreeMap<>(parameters);
        for (Map.Entry<String, List<String>> entry : sortedParameters.entrySet()) {
            List<String> values = entry.getValue();
            for (String value : values.stream().sorted().collect(Collectors.toList())) {
                requestToSign += entry.getKey() + "=" + value + ",";
            }
        }
        requestToSign.substring(0, requestToSign.length()-1);
        requestToSign += ";";

        // headers
        TreeMap<String, String> sortedHeaders = new TreeMap<>(headers);
        for (Map.Entry<String, String> entry : sortedHeaders.entrySet()) {
            requestToSign += entry.getKey() + "=" + entry.getValue() + ",";
        }
        requestToSign.substring(0, requestToSign.length()-1);
        requestToSign += ";";

        // data
        return combineByteArrays(requestToSign.getBytes(StandardCharsets.UTF_8), data);
    }

    static private byte[] combineByteArrays(byte[] byteArray1, byte[] byteArray2) {
        byte[] combined = new byte[byteArray1.length + byteArray2.length];

        for (int i = 0; i < combined.length; ++i)
        {
            combined[i] = i < byteArray1.length ? byteArray1[i] : byteArray2[i - byteArray1.length];
        }

        return combined;
    }
}
