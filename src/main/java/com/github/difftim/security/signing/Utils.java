package com.github.difftim.security.signing;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class Utils {
    static public String getNonce() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    static public byte[] getPreSignedData(String appid, long timestamp, String nonce, byte[] data) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        result.write((String.join(";", appid, String.valueOf(timestamp), nonce) + ";").getBytes(StandardCharsets.UTF_8));
        result.write(data);
        return result.toByteArray();
    }

    static public String sign(String algorithm, byte[] key, byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(algorithm);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, algorithm);
        mac.init(secretKeySpec);
        return byteArrayToHex(mac.doFinal(data));
    }

    static public String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
