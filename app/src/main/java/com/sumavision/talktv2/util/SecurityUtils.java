package com.sumavision.talktv2.util;

/**
 * Created by sharpay on 16-9-20.
 */

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * <p><description></description></p>
 * @author eric.chao
 * @version 1.0
 * @since JDK 1.7
 * @copyright Copyright 2015 TVFan All rights reserved.
 */
public final class SecurityUtils {

    //private final static Logger logger = Logger.getLogger(SecurityUtils.class);

    private static final String DIGEST_ALGORITHM = "MD5";
    private static final String ENCRYPT_ALGORITHM = "AES";
    private static final String CIPHER = "AES/CBC/PKCS5Padding";
    private static final String ENCODING = "utf-8";
    private static final byte[] iv = {0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};

    //used to encrypt client server communication, length must be 16.
    public static final String RELEASE_KEY = "09Z18tvFaNmobILe";

    private SecurityUtils(){}

    /** encode 加密 */
    public static String encode(String source, String key) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER);
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(ENCODING), ENCRYPT_ALGORITHM);
        IvParameterSpec paramSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
        byte[] result = cipher.doFinal(source.getBytes(ENCODING));
        String encoded = encodeBase64String(result);
        return encoded;
    }

    /** decode 解密*/
    public static String decode(String encrypted, String key) throws Exception {

        Cipher cipher = Cipher.getInstance(CIPHER);
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(ENCODING), ENCRYPT_ALGORITHM);
        IvParameterSpec paramSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
        byte[] bytes = decodeBase64String(encrypted);
        return new String(cipher.doFinal(bytes) ,ENCODING);

    }

    public static String encodeClientKey(String clientKey) throws Exception {
        return encode(clientKey, RELEASE_KEY);
    }

    public static String decodeClientKey(String clientKey) throws Exception {
        return decode(clientKey, RELEASE_KEY);
    }

    public static String getDigest(String src) throws Exception {
        MessageDigest md = MessageDigest.getInstance(DIGEST_ALGORITHM);
        byte[] bytes = src.getBytes(ENCODING);
        md.update(bytes, 0, bytes.length);
        byte[] digest = md.digest();

        StringBuilder sb = new StringBuilder();
        for(byte b : digest){
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    private static String encodeBase64String(byte[] bytes){
        return Base64.encode(bytes);
    }

    private static byte[] decodeBase64String(String s){
        return Base64.decode(s);
    }
}