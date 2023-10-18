package com.github.difftim.security.encryption;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AES {

    final private Cipher encryptCipher;
    final private Cipher decryptCipher;
    final private static java.util.Base64.Encoder base64Encoder = Base64.getEncoder();
    final private static java.util.Base64.Decoder base64Decoder = Base64.getDecoder();

    public AES(String algorithm, String key, String iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        SecretKey secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
        this.encryptCipher = Cipher.getInstance(algorithm);
        this.encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        this.decryptCipher = Cipher.getInstance(algorithm);
        this.decryptCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"), new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));
    }

    public String encryptString(String plainText) throws IllegalBlockSizeException, BadPaddingException {
        byte[] cipherBinary = encryptCipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return base64Encoder.encodeToString(cipherBinary);
    }

    public String decryptString(String cipherText) throws IllegalBlockSizeException, BadPaddingException {
        byte[] cipherBinary = base64Decoder.decode(cipherText);
        byte[] plainBinary = decryptCipher.doFinal(cipherBinary);
        return new String(plainBinary, StandardCharsets.UTF_8);
    }
}
