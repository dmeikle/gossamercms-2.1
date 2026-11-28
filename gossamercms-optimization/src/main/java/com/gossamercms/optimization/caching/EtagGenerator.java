package com.gossamercms.optimization.caching;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class EtagGenerator {

    public String generate(String content) {
        return generate(content.getBytes(StandardCharsets.UTF_8));
    }

    public String generate(byte[] content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content);
            return "\"" + HexFormat.of().formatHex(hash) + "\"";
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to generate ETag", e);
        }
    }
}
