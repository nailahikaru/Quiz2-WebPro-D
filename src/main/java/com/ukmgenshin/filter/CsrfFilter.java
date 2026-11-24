package com.ukmgenshin.filter;

import com.ukmgenshin.util.Csrf;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.regex.Pattern;

/** Menolak POST tanpa token CSRF yang cocok, dan menyediakan token ke JSP lewat sessionScope.csrf. */
public class CsrfFilter implements Filter {

    private static final Pattern STATIC_FILE =
            Pattern.compile(".*\\.(css|js|png|jpe?g|gif|webp|svg|ico|woff2?|map)$", Pattern.CASE_INSENSITIVE);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        if ("POST".equalsIgnoreCase(req.getMethod())) {
            if (!Csrf.isValid(req)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Token keamanan tidak valid atau sesi sudah habis. Silakan muat ulang halaman lalu coba lagi.");
                return;
            }
        } else {
            String path = req.getRequestURI();
            if (!STATIC_FILE.matcher(path).matches() && !path.contains("/uploads/")) {
                Csrf.token(req.getSession(true));
            }
        }
        chain.doFilter(request, response);
    }
}
