package com.mintit.incentive.common.util;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CommonUtil {

    public static String shortUUID() {
        return Long.toString(ByteBuffer.wrap(UUID.randomUUID().toString().getBytes())
                                       .getLong(), Character.MAX_RADIX);
    }

    public static int certNumber() {
        SecureRandom secureRandomGenerator = null;
        try {
            secureRandomGenerator = SecureRandom.getInstance("SHA1PRNG");
            byte[] randomBytes = new byte[128];
            secureRandomGenerator.nextBytes(randomBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return secureRandomGenerator.nextInt(999999);
    }
}
