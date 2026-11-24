package com.ukmgenshin.servlet;

import com.ukmgenshin.filter.AuthFilter;
import com.ukmgenshin.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

/** Fungsi bantu bersama untuk semua servlet (pengganti Controller dasar di Laravel). */
public abstract class BaseServlet extends HttpServlet {

    private static final String FLASH_KEY = "flashSuccess";

    /** Bagian setelah nama servlet: "/edit" -> "edit"; tanpa path -> "". */
    protected String action(HttpServletRequest req) {
        String info = req.getPathInfo();
        if (info == null || info.equals("/")) {
            return "";
        }
        return info.substring(1);
    }

    /** Tampilkan JSP di WEB-INF/views. "nav" dipakai navbar untuk menandai menu aktif. */
    protected void view(HttpServletRequest req, HttpServletResponse resp, String jsp, String nav)
            throws ServletException, IOException {
        req.setAttribute("nav", nav);
        HttpSession session = req.getSession(false);
        if (session != null) {
            Object message = session.getAttribute(FLASH_KEY);
            if (message != null) {
                req.setAttribute("success", message);
                session.removeAttribute(FLASH_KEY);
            }
        }
        req.getRequestDispatcher("/WEB-INF/views/" + jsp + ".jsp").forward(req, resp);
    }

    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        resp.sendRedirect(req.getContextPath() + path);
    }

    /** Pesan sukses yang tampil sekali setelah redirect (with('success', ...) di Laravel). */
    protected void flash(HttpServletRequest req, String message) {
        req.getSession(true).setAttribute(FLASH_KEY, message);
    }

    protected User currentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (User) session.getAttribute(AuthFilter.SESSION_USER);
    }

    protected Long currentUserId(HttpServletRequest req) {
        User u = currentUser(req);
        return u == null ? null : u.getId();
    }

    /** Nilai parameter yang sudah di-trim; "" jika tidak ada. */
    protected String param(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? "" : v.trim();
    }

    /** Parameter angka (id); null jika kosong atau bukan angka. */
    protected Long longParam(HttpServletRequest req, String name) {
        try {
            return Long.valueOf(param(req, name));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected LocalDate dateParam(HttpServletRequest req, String name) {
        try {
            return LocalDate.parse(param(req, name));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    protected LocalTime timeParam(HttpServletRequest req, String name) {
        try {
            return LocalTime.parse(param(req, name));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /** Simpan input form agar bisa ditampilkan lagi saat validasi gagal (old() di Laravel). */
    protected void keepOld(HttpServletRequest req) {
        Map<String, String> old = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> e : req.getParameterMap().entrySet()) {
            if (e.getValue().length > 0 && !e.getKey().equals("_csrf")) {
                old.put(e.getKey(), e.getValue()[0]);
            }
        }
        req.setAttribute("old", old);
    }

    protected void notFound(HttpServletResponse resp) throws IOException {
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}
