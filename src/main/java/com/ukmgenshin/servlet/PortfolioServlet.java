package com.ukmgenshin.servlet;

import com.ukmgenshin.dao.PortfolioDao;
import com.ukmgenshin.model.Portfolio;
import com.ukmgenshin.model.PortfolioImage;
import com.ukmgenshin.util.AppConfig;
import com.ukmgenshin.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/** CRUD Portofolio + upload 1-5 gambar (PortfolioController). */
@WebServlet("/portfolios/*")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 10L * 1024 * 1024, maxRequestSize = 60L * 1024 * 1024)
public class PortfolioServlet extends BaseServlet {

    private static final String NAV = "portfolios";
    private static final int MAX_IMAGES = 5;
    private static final long MAX_SIZE = 10L * 1024 * 1024;
    private static final Set<String> EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    private final PortfolioDao dao = new PortfolioDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (action(req)) {
            case "":
                req.setAttribute("portfolios", dao.findAll());
                view(req, resp, "portfolios/index", NAV);
                break;
            case "create":
                view(req, resp, "portfolios/form", NAV);
                break;
            case "edit": {
                Portfolio p = dao.find(longParam(req, "id"));
                if (p == null) {
                    notFound(resp);
                    return;
                }
                req.setAttribute("portfolio", p);
                view(req, resp, "portfolios/form", NAV);
                break;
            }
            default:
                notFound(resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (action(req)) {
            case "store":
                save(req, resp, null);
                break;
            case "update": {
                Portfolio p = dao.find(longParam(req, "id"));
                if (p == null) {
                    notFound(resp);
                    return;
                }
                save(req, resp, p);
                break;
            }
            case "image-delete":
                deleteImage(req, resp);
                break;
            case "delete":
                delete(req, resp);
                break;
            default:
                notFound(resp);
        }
    }

    private void save(HttpServletRequest req, HttpServletResponse resp, Portfolio existing)
            throws ServletException, IOException {
        Validator v = new Validator();
        List<Part> files = new ArrayList<>();
        try {
            for (Part part : req.getParts()) {
                if (part.getName().equals("images") && part.getSize() > 0 && hasName(part)) {
                    files.add(part);
                }
            }
        } catch (IllegalStateException e) {
            v.add("images", "Ukuran gambar terlalu besar (maksimal 10 MB per gambar).");
        }

        String title = param(req, "title");
        String description = param(req, "description");
        v.required("title", title, "Judul karya").max("title", title, 150, "Judul karya");

        int existingCount = existing == null ? 0 : existing.getImages().size();
        if (existing == null && files.isEmpty()) {
            v.add("images", "Pilih minimal 1 gambar.");
        }
        if (existingCount + files.size() > MAX_IMAGES) {
            v.add("images", "Maksimal " + MAX_IMAGES + " gambar per portofolio.");
        }
        for (Part part : files) {
            if (!EXTENSIONS.contains(extension(part.getSubmittedFileName()))
                    || part.getContentType() == null || !part.getContentType().startsWith("image/")) {
                v.add("images", "Gambar harus berformat JPG, PNG, atau WEBP.");
            } else if (part.getSize() > MAX_SIZE) {
                v.add("images", "Ukuran gambar maksimal 10 MB.");
            }
        }

        if (v.fails()) {
            req.setAttribute("errors", v.errors());
            req.setAttribute("portfolio", existing);
            keepOld(req);
            view(req, resp, "portfolios/form", NAV);
            return;
        }

        List<String> saved = storeFiles(files);
        String desc = description.isEmpty() ? null : description;
        if (existing == null) {
            dao.create(currentUserId(req), title, desc, saved);
            flash(req, "Portofolio berhasil ditambahkan.");
        } else {
            dao.update(existing.getId(), title, desc);
            if (!saved.isEmpty()) {
                dao.addImages(existing.getId(), saved);
            }
            flash(req, "Portofolio berhasil diperbarui.");
        }
        redirect(req, resp, "/portfolios");
    }

    /** Hapus satu gambar; portofolio harus tetap punya minimal 1 gambar. */
    private void deleteImage(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        PortfolioImage image = dao.findImage(longParam(req, "image_id"));
        if (image == null) {
            notFound(resp);
            return;
        }
        Portfolio p = dao.find(image.getPortfolioId());
        if (p.getImages().size() <= 1) {
            Validator v = new Validator();
            v.add("images", "Portofolio harus punya minimal 1 gambar. Hapus portofolionya jika tidak diperlukan.");
            req.setAttribute("errors", v.errors());
            req.setAttribute("portfolio", p);
            view(req, resp, "portfolios/form", NAV);
            return;
        }
        dao.deleteImage(image.getId());
        deleteFile(image.getPath());
        flash(req, "Gambar berhasil dihapus.");
        redirect(req, resp, "/portfolios/edit?id=" + p.getId());
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Portfolio p = dao.find(longParam(req, "id"));
        if (p == null) {
            notFound(resp);
            return;
        }
        dao.delete(p.getId());
        for (PortfolioImage image : p.getImages()) {
            deleteFile(image.getPath());
        }
        flash(req, "Portofolio berhasil dihapus.");
        redirect(req, resp, "/portfolios");
    }

    // ---------- file ----------

    private static boolean hasName(Part part) {
        String name = part.getSubmittedFileName();
        return name != null && !name.isBlank();
    }

    private static String extension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? "" : fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private List<String> storeFiles(List<Part> files) throws IOException {
        List<String> paths = new ArrayList<>();
        if (files.isEmpty()) {
            return paths;
        }
        Path dir = AppConfig.uploadDir().resolve("portfolios");
        Files.createDirectories(dir);
        for (Part part : files) {
            String name = UUID.randomUUID().toString().replace("-", "") + "." + extension(part.getSubmittedFileName());
            try (InputStream in = part.getInputStream()) {
                Files.copy(in, dir.resolve(name));
            }
            paths.add("portfolios/" + name);
        }
        return paths;
    }

    private void deleteFile(String relativePath) {
        try {
            Path base = AppConfig.uploadDir();
            Path file = base.resolve(relativePath).normalize();
            if (file.startsWith(base)) {
                Files.deleteIfExists(file);
            }
        } catch (IOException ignored) {
            // file sudah hilang / tidak bisa dihapus: abaikan, data di database sudah bersih
        }
    }
}
