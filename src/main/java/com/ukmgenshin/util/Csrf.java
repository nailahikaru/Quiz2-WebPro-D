package com.ukmgenshin.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** Token CSRF sederhana: satu token per sesi, dikirim lewat input hidden "_csrf". */
public final class Csrf {

    public static final String SESSION_KEY = "csrf";
    public static final String PARAM = "_csrf";

    private static final SecureRandom RANDOM = new SecureRandom();

    private Csrf() {
    }

    public static String token(HttpSession session) {
        String token = (String) session.getAttribute(SESSION_KEY);
        if (token == null) {
            token = newToken();
            session.setAttribute(SESSION_KEY, token);
        }
        return token;
    }

    public static void regenerate(HttpSession session) {
        session.setAttribute(SESSION_KEY, newToken());
    }

    public static boolean isValid(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return false;
        }
        String expected = (String) session.getAttribute(SESSION_KEY);
        String actual = req.getParameter(PARAM);
        if (expected == null || actual == null) {
            return false;
        }
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8));
    }

    private static String newToken() {
        byte[] bytes = new byte[24];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
