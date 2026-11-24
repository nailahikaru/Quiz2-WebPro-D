package com.ukmgenshin.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(10));
    }

    public static boolean matches(String plain, String hash) {
        try {
            return hash != null && BCrypt.checkpw(plain, hash);
        } catch (IllegalArgumentException e) {
            return false; // format hash tidak dikenal
        }
    }
}
