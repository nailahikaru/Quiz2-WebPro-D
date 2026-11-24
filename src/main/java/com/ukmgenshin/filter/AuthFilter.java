package com.ukmgenshin.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Set;

/**
 * Padanan middleware "auth" di Laravel. Hampir semua rute wajib login; yang boleh dilihat tamu
 * hanya daftar pengumuman, daftar portofolio, daftar event, dan detail event.
 */
public class AuthFilter implements Filter {

    public static final String SESSION_USER = "authUser";
    public static final String INTENDED = "intendedUrl";

    private static final Set<String> PUBLIC_GET = Set.of(
            "/announcements", "/portfolios", "/events", "/events/show");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getServletPath() + (req.getPathInfo() == null ? "" : req.getPathInfo());
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        boolean isPublic = "GET".equalsIgnoreCase(req.getMethod()) && PUBLIC_GET.contains(path);
        HttpSession session = req.getSession(false);
        boolean loggedIn = session != null && session.getAttribute(SESSION_USER) != null;

        if (isPublic || loggedIn) {
            chain.doFilter(request, response);
            return;
        }

        // Simpan tujuan awal supaya setelah login kembali ke halaman ini (redirect()->intended()).
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            String target = req.getRequestURI();
            if (req.getQueryString() != null) {
                target += "?" + req.getQueryString();
            }
            req.getSession(true).setAttribute(INTENDED, target);
        }
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
