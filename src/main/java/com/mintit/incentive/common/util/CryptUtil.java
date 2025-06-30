package com.mintit.incentive.common.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CryptUtil {

    private static final Logger logger = LoggerFactory.getLogger(CryptUtil.class);
    private static final Charset ENCODING_TYPE = StandardCharsets.UTF_8;

    private static final String INSTANCE_TYPE = "AES/CBC/PKCS5Padding";

    private static SecretKeySpec secretKeySpec;

    private static Cipher cipher;

    private static String key;

    @Value("${enc.key}")
    public void setKey(String value) {
        key = value;
    }

    private static String longKey;
    private static String saltKey;

    @Value("${enc.key.256}")
    public void setLongKey(String value) {
        longKey = value;
    }

    private static IvParameterSpec ivParameterSpec;

    public static void Aes128() {
        validation(key);
        try {
            if (secretKeySpec == null && cipher == null && ivParameterSpec == null) {
                byte[] keyBytes = key.getBytes(ENCODING_TYPE);
                secretKeySpec = new SecretKeySpec(keyBytes, "AES");
                cipher = Cipher.getInstance(INSTANCE_TYPE);
                ivParameterSpec = new IvParameterSpec(keyBytes);
            }
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            logger.error("AES128 Error ");
            logger.debug(e.getMessage());
        }
    }

    public static String encrypt(final String str) throws Exception {
        Aes128();
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(str.getBytes(ENCODING_TYPE));
        return new String(Base64.getEncoder().encode(encrypted), ENCODING_TYPE);
    }

    public static String decrypt(final String str) throws Exception {
        Aes128();
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decoded = Base64.getDecoder().decode(str.getBytes(ENCODING_TYPE));
        return new String(cipher.doFinal(decoded), ENCODING_TYPE);
    }

    private static void validation(final String key) {
        Optional.ofNullable(key).orElseThrow(IllegalArgumentException::new);
    }


    /**
     * AES 암호화
     *
     * @param key  : 암호화 key
     * @param text : 평문
     * @return : 암호문
     * @throws Exception
     */
    public static String encryptAES256(String key, String text) throws Exception {
        byte[] base64Encoded;
        try {
            Cipher cipher = Cipher.getInstance(INSTANCE_TYPE);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.substring(0, 32)
                                                                  .getBytes(), "AES"), new IvParameterSpec(key.substring(0, 16)
                                                                                                              .getBytes()));

            //           byte[] encrypted = cipher.doFinal(text.getBytes(Charset.forName("EUC-KR")));
            byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            base64Encoded = org.apache.commons.codec.binary.Base64.encodeBase64(encrypted);
        } catch (Exception e) {
            logger.error("encryptAES256 {}", e.getMessage());
            base64Encoded = org.apache.commons.lang3.StringUtils.EMPTY.getBytes();
        }

        //       return new String(base64Encoded, Charset.forName("EUC-KR"));
        return new String(base64Encoded, StandardCharsets.UTF_8);
    }

    /**
     * AES 암호화 암호화 key : class member variable 활용
     *
     * @param text : 평문
     * @return : 암호문
     * @throws Exception
     */
    public static String encryptAES256(String text) throws Exception {
        return encryptAES256(longKey, text);
    }

    public static String encryptAES256Salt(String text) throws Exception {
        return encryptAES256(saltKey, text);
    }

    /**
     * AES256복호화
     *
     * @param key  : 복호화 key
     * @param text : 암호문
     * @return : 평문
     */
    public static String decryptAES256(String key, String text) throws Exception {
        byte[] decrypted;
        try {
            Cipher cipher = Cipher.getInstance(INSTANCE_TYPE);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.substring(0, 32)
                                                                  .getBytes(), "AES"), new IvParameterSpec(key.substring(0, 16)
                                                                                                              .getBytes()));

            //       byte[] base64Decoded = org.apache.commons.codec.binary.Base64.decodeBase64(text.getBytes(Charset.forName("EUC-KR")));
            byte[] base64Decoded = org.apache.commons.codec.binary.Base64.decodeBase64(text.getBytes(StandardCharsets.UTF_8));
            decrypted = cipher.doFinal(base64Decoded);
        } catch (Exception e) {
            logger.error("decryptAES256 {}", e.getMessage());
            decrypted = org.apache.commons.lang3.StringUtils.EMPTY.getBytes();
        }
        //       return new String(decrypted, Charset.forName("EUC-KR"));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /**
     * AES256 복호화 복호화 key : class member variable 활용
     *
     * @param text : 암호문
     * @return : 평문
     * @throws Exception
     */
    public static String decryptAES256(String text) throws Exception {
        return decryptAES256(longKey, text);
    }


    public static String base64Encode(String text) {
        if (!StringUtils.hasText(text)) {
            return "";
        }
        byte[] targetBytes = text.getBytes();

        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(targetBytes);

        return new String(encodedBytes);
    }

    public static String base64Decode(String text) {
        if (!StringUtils.hasText(text)) {
            return "";
        }
        byte[] targetBytes = text.getBytes();

        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(targetBytes);

        return new String(decodedBytes);
    }
}