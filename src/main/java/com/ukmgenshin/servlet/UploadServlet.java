package com.ukmgenshin.servlet;

import com.ukmgenshin.util.AppConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

/** Menyajikan gambar upload dari folder di luar WAR (hanya file gambar). */
@WebServlet("/uploads/*")
public class UploadServlet extends HttpServlet {

    private static final Map<String, String> TYPES = Map.of(
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png",
            "webp", "image/webp",
            "gif", "image/gif");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String info = req.getPathInfo();
        if (info == null || info.length() < 2) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Path base = AppConfig.uploadDir();
        Path file = base.resolve(info.substring(1)).normalize();
        String name = file.getFileName() == null ? "" : file.getFileName().toString();
        int dot = name.lastIndexOf('.');
        String type = dot < 0 ? null : TYPES.get(name.substring(dot + 1).toLowerCase(Locale.ROOT));

        // normalize + startsWith mencegah path traversal ("../")
        if (type == null || !file.startsWith(base) || !Files.isRegularFile(file)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        resp.setContentType(type);
        resp.setContentLengthLong(Files.size(file));
        resp.setHeader("Cache-Control", "public, max-age=86400");
        resp.setHeader("X-Content-Type-Options", "nosniff");
        Files.copy(file, resp.getOutputStream());
    }
}
