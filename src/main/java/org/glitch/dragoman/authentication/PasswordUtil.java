package org.glitch.dragoman.authentication;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {

    public String toHash(String password) {
        try {
            // MessageDigest isn't thread safe so we must create a new one each time we want to hash a password
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

            sha256.update(password.getBytes("UTF-8"));

            return toHexString(sha256.digest());
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("No SHA-256 algorithm available, check the JRE!", ex);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("UTF-8 is not supported by this platform!", ex);
        }
    }

    private String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);

            if (hex.length() == 1) {
                hexString.append('0');
            }

            hexString.append(hex);
        }
        return hexString.toString();
    }
}