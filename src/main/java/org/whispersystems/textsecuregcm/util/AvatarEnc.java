package org.whispersystems.textsecuregcm.util;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.model.OSSObject;
import com.google.gson.Gson;
import io.netty.handler.codec.base64.Base64Decoder;
import okhttp3.*;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.s3.UrlSignerAli;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AvatarEnc {
    static private final Logger logger = LoggerFactory.getLogger(AvatarEnc.class);

    static public HashMap<String, Object> uploadAvatar(byte[] avatar, UrlSignerAli urlSignerAli ) throws IOException {
        // 生成随机密钥
        byte[] secret = getSecretBytes(32);
        // 上传
        final byte[] encrypt = AESUtils.gcmEncrypt(avatar, secret);

        long attachmentId = generateAttachmentId();
        URL url = urlSignerAli.getPreSignedUrl(attachmentId, HttpMethod.PUT);
        RequestBody body = RequestBody.create(encrypt, null);
        Response response = putAvatarData(url, body);
        if (!response.isSuccessful()) {
            logger.warn("uploadAvatar failed,response:{},url:{}",response,url.toExternalForm());
            throw new IOException("Error HTTP code:"+response.code());
        }
        HashMap<String, Object> avatarInfo = new HashMap<>();
        avatarInfo.put("attachmentId", String.valueOf(attachmentId));
        avatarInfo.put("encAlgo", "AESGCM256");
        avatarInfo.put("encKey", new String(Base64.getEncoder().encode(secret)));

        return avatarInfo;
    }

    private static Response putAvatarData(URL url, RequestBody body) throws IOException {
        Request request = new Request.Builder()
                .url(url.toExternalForm())
                .method("PUT", body)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .readTimeout(5, TimeUnit.MINUTES).build();
        Response response = okHttpClient.newCall(request).execute();
        final ResponseBody responseBody = response.body();
        if (responseBody != null) responseBody.close();
        return  response;
    }

    static public byte[] downloadAvatar(String attachmentId, String encKey, UrlSignerAli urlSignerAli) throws IOException{
        URL url = urlSignerAli.getPreSignedUrl(Long.valueOf(attachmentId), HttpMethod.GET);
        Request request = new Request.Builder()
                .url(url.toExternalForm()).get()
                .build();
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .readTimeout(5, TimeUnit.MINUTES).build();
        Response response = okHttpClient.newCall(request).execute();
        ResponseBody body = response.body();
        byte[] bytes = body.bytes();
        byte[] decode = Base64.getDecoder().decode(encKey);
        return AESUtils.decrypt(bytes, decode);
    }

    static public String uploadGroupAvatar(byte[] avatar, UrlSignerAli urlSignerAli) throws InvalidAlgorithmParameterException,
            IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IOException {
        byte[] secret = getSecretBytes(64);
        final byte[] encKey = new byte[32];
        final byte[] macKey = new byte[32];
        System.arraycopy(secret,0,encKey,0,32);
        System.arraycopy(secret,32,macKey,0,32);
        final cbcEncResult cbcEncResult = AESUtils.cbcEncrypt(avatar, encKey, macKey);
        long attachmentId = generateAttachmentId();
        URL url = urlSignerAli.getPreSignedUrl(attachmentId, HttpMethod.PUT);
        RequestBody body = RequestBody.create(cbcEncResult.cipherData, null);
        Response response = putAvatarData(url, body);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            logger.warn("uploadGroupAvatar size:{}, md5:{}, response:{}, url:{}",
                    avatar.length, Hex.encodeHexString( md.digest(avatar)), response, url.toExternalForm());
        } catch (Exception e) {
            logger.error("md5 Exception",e);
        }
        if (!response.isSuccessful()) {
            logger.warn("uploadGroupAvatar failed,response:{},url:{}",response,url.toExternalForm());
            throw new IOException("Error HTTP code:"+response.code());
        }

        final groupAvatar groupAvatar = new groupAvatar();
        groupAvatar.setByteCount(String.valueOf(avatar.length));
        groupAvatar.setDigest(new String(Base64.getEncoder().encode(cbcEncResult.digest)));
        groupAvatar.setEncryptionKey(new String(Base64.getEncoder().encode(secret)));
        groupAvatar.setServerId(String.valueOf(attachmentId));
        groupAvatar.setContentType("image/png");
        return new Gson().toJson(new dataWrap( new String(Base64.getEncoder().encode(
                new Gson().toJson(groupAvatar).getBytes(StandardCharsets.UTF_8)))));
    }
    static class dataWrap{
        String data;

        public dataWrap(String data) {
            this.data = data;
        }
    }

    static public long generateAttachmentId() {
        byte[] attachmentBytes = new byte[8];
        new SecureRandom().nextBytes(attachmentBytes);

        attachmentBytes[0] = (byte) (attachmentBytes[0] & 0x7F);
        return Conversions.byteArrayToLong(attachmentBytes);
    }

    public static String getSecret(int size) {
        byte[] secret = getSecretBytes(size);
        return new String(Base64.getEncoder().encode(secret));
    }

    public static byte[] getSecretBytes(int size) {
        byte[] secret = new byte[size];
        getSecureRandom().nextBytes(secret);
        return secret;
    }

    private static SecureRandom getSecureRandom() {
        try {
            return SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
    }

    static class cbcEncResult{
        public byte[] getCipherData() {
            return cipherData;
        }

        public byte[] getDigest() {
            return digest;
        }

        byte[] cipherData;
        byte[] digest;

        public cbcEncResult(byte[] cipherData, byte[] digest) {
            this.cipherData = cipherData;
            this.digest = digest;
        }
    }

    static public class groupAvatar {
        private String byteCount;
        private String digest;
        private String encryptionKey;
        private String serverId;
        private int attachmentType = 0;
        private String contentType;

        public void setByteCount(String byteCount) {
            this.byteCount = byteCount;
        }

        public void setDigest(String digest) {
            this.digest = digest;
        }

        public void setEncryptionKey(String encryptionKey) {
            this.encryptionKey = encryptionKey;
        }

        public void setServerId(String serverId) {
            this.serverId = serverId;
        }

        public void setAttachmentType(int attachmentType) {
            this.attachmentType = attachmentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }
    }

    static class AESUtils {
        private final static Logger logger = LoggerFactory.getLogger(AESUtils.class);


        private static final String KEY_ALGORITHM = "AES";
        private static final String GCM_CIPHER_ALGORITHM = "AES/GCM/NoPadding";// 默认的加密算法
        private static final String CBC_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";// 默认的加密算法

        private static final String CHARSET = "UTF-8";

        /**
         * AES 加密操作
         *
         * @param contentBytes 待加密内容
         * @param encryptPass  加密密码
         * @return 返回Base64转码后的加密数据
         */
        public static byte[] gcmEncrypt(byte[] contentBytes, byte[] encryptPass) {
            try {
                byte[] iv = new byte[12];
                SecureRandom secureRandom = new SecureRandom();
                secureRandom.nextBytes(iv);
                Cipher cipher = Cipher.getInstance(GCM_CIPHER_ALGORITHM);
                GCMParameterSpec params = new GCMParameterSpec(128, iv);
                cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(encryptPass), params);
                byte[] encryptData = cipher.doFinal(contentBytes);
                assert encryptData.length == contentBytes.length + 16;
                byte[] message = new byte[12 + contentBytes.length + 16];
                System.arraycopy(iv, 0, message, 0, 12);
                System.arraycopy(encryptData, 0, message, 12, encryptData.length);
                return message;
                //return Base64.getEncoder().encodeToString(message);
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                    | BadPaddingException e) {
                logger.error(e.getMessage(), e);
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static cbcEncResult cbcEncrypt(byte[] contentBytes, byte[] encryptPass, byte[] macKey ) throws
                NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException,
                NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
            byte[] iv = new byte[16];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance(CBC_CIPHER_ALGORITHM);
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(encryptPass), paramSpec);
            byte[] cipherData = cipher.doFinal(contentBytes);
            byte[] ivCipherData = new byte[iv.length + cipherData.length];
            System.arraycopy(iv,0,ivCipherData,0,iv.length);
            System.arraycopy(cipherData,0,ivCipherData, iv.length, cipherData.length);
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(macKey, "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] macData = mac.doFinal(ivCipherData);
            final byte[] all = new byte[ivCipherData.length + macData.length];
            System.arraycopy(ivCipherData,0,all,0,ivCipherData.length);
            System.arraycopy(macData,0,all,ivCipherData.length,macData.length);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return new cbcEncResult(all,digest.digest(all));
        }
        /**
         * AES 解密操作
         *
         * @param base64Content
         * @param encryptPass
         * @return
         */
        public static byte[] decrypt(byte[] encryptContent, byte[] encryptPass) {
            try {
                if (encryptContent.length < 12 + 16)
                    throw new IllegalArgumentException();
                GCMParameterSpec params = new GCMParameterSpec(128, encryptContent, 0, 12);
                Cipher cipher = Cipher.getInstance(GCM_CIPHER_ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, getSecretKey(encryptPass), params);
                byte[] decryptData = cipher.doFinal(encryptContent, 12, encryptContent.length - 12);
                return decryptData;
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
                    | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
                logger.error(e.getMessage(), e);
            }
            return null;
        }

        /**
         * 生成加密秘钥
         *
         * @return
         * @throws NoSuchAlgorithmException
         */
        private static SecretKeySpec getSecretKey(byte[] encryptPass) throws NoSuchAlgorithmException {
            //KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
            //// 初始化密钥生成器，AES要求密钥长度为128位、192位、256位
            //SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            //secureRandom.setSeed(encryptPass);
            //kg.init(256, secureRandom);
            //SecretKey secretKey = kg.generateKey();
            return new SecretKeySpec(encryptPass, KEY_ALGORITHM);// 转换为AES专用密钥
            //return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);// 转换为AES专用密钥
        }

    }
}
