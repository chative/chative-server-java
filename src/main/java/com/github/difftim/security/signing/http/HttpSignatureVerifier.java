package com.github.difftim.security.signing.http;

import com.github.difftim.security.signing.SignatureVerifier;
import com.github.difftim.security.utils.Utils;
import io.netty.util.internal.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.github.difftim.security.signing.http.Constants.*;

public class HttpSignatureVerifier {

    static public class InvalidSignatureException extends RuntimeException {
        public InvalidSignatureException(String message) {
        super(message);
    }
    }

    final private SignatureVerifier signatureVerifier;

    public HttpSignatureVerifier(SignatureVerifier.KeyStorage keyStorage) {
        this.signatureVerifier = new SignatureVerifier(keyStorage);
    }

    public void verify(String method, String uri, Map<String, List<String>> parameters, Map<String, String> allHeaders, byte[] data, String sourceIP) throws InvalidSignatureException {

        // Lower cases
        allHeaders = Utils.mapToLowerCase(allHeaders);

        String appid = allHeaders.get(HEADER_NAME_APPID.toLowerCase(Locale.ROOT));
        String timestamp = allHeaders.get(HEADER_NAME_TIMESTAMP.toLowerCase(Locale.ROOT));
        String nonce = allHeaders.get(HEADER_NAME_NONCE.toLowerCase(Locale.ROOT));
        String signedHeaders = allHeaders.get(HEADER_NAME_SIGNEDHEADERS.toLowerCase(Locale.ROOT)); // can be null
        String signature = allHeaders.get(HEADER_NAME_SIGNATURE.toLowerCase(Locale.ROOT));
        if (StringUtil.isNullOrEmpty(appid) || StringUtil.isNullOrEmpty(timestamp) || StringUtil.isNullOrEmpty(nonce) || StringUtil.isNullOrEmpty(signature)) {
            throw new InvalidSignatureException("appid, timestamp, nonce, algorithm, signedHeaders or signature headers are missing");
        }

        // headers
        Map<String, String> participatedHeaders = new HashMap<>();
        if (null != signedHeaders) {
            participatedHeaders = filterHeaders(signedHeaders, allHeaders);
        }

        // build data
        byte[] dataToSign = HttpSigner.buildData(method, uri, parameters, participatedHeaders, data);

        try {
            signatureVerifier.verify(appid, Long.valueOf(timestamp), nonce, signature, dataToSign, sourceIP);
        } catch (SignatureVerifier.InvalidAppIDException e) {
            throw new InvalidSignatureException("invalid appid");
        } catch (SignatureVerifier.ExpiredException e) {
            throw new InvalidSignatureException("signature has expired");
        } catch (SignatureVerifier.SourceIPNotAllowedException e) {
            throw new InvalidSignatureException("source IP is not allowed");
        } catch (SignatureVerifier.InvalidSignatureException e) {
            throw new InvalidSignatureException("invalid signature");
        } catch (SignatureVerifier.ReplayRequestException e) {
            throw new InvalidSignatureException("replay request");
        } catch (SignatureVerifier.ServerErrorException e) {
            throw new InvalidSignatureException("unknown server error occurred during verifying signature");
        }
    }

    private Map<String, String> filterHeaders(String participatedHeaders, Map<String, String> allHeaders) throws InvalidSignatureException {
        Map<String, String> result = new HashMap<>();
        for (String participatedHeaderKey : participatedHeaders.split(",")) {
            if (!allHeaders.containsKey(participatedHeaderKey)) {
                throw new InvalidSignatureException("absent header: " + participatedHeaderKey);
            }
            String value = allHeaders.get(participatedHeaderKey);
            result.put(participatedHeaderKey, value);
        }

        return result;
    }
}
