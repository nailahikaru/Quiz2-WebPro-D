package com.ukmgenshin.servlet;

import com.ukmgenshin.dao.AnnouncementDao;
import com.ukmgenshin.model.Announcement;
import com.ukmgenshin.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** CRUD Pengumuman (AnnouncementController). Daftar bisa dilihat siapa saja, sisanya wajib login. */
@WebServlet("/announcements/*")
public class AnnouncementServlet extends BaseServlet {

    private static final String NAV = "announcements";
    private final AnnouncementDao dao = new AnnouncementDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (action(req)) {
            case "":
                req.setAttribute("announcements", dao.findAll());
                view(req, resp, "announcements/index", NAV);
                break;
            case "create":
                view(req, resp, "announcements/form", NAV);
                break;
            case "edit": {
                Announcement a = dao.find(longParam(req, "id"));
                if (a == null) {
                    notFound(resp);
                    return;
                }
                req.setAttribute("announcement", a);
                view(req, resp, "announcements/form", NAV);
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
                Announcement a = dao.find(longParam(req, "id"));
                if (a == null) {
                    notFound(resp);
                    return;
                }
                save(req, resp, a);
                break;
            }
            case "delete": {
                Announcement a = dao.find(longParam(req, "id"));
                if (a == null) {
                    notFound(resp);
                    return;
                }
                dao.delete(a.getId());
                flash(req, "Pengumuman berhasil dihapus.");
                redirect(req, resp, "/announcements");
                break;
            }
            default:
                notFound(resp);
        }
    }

    private void save(HttpServletRequest req, HttpServletResponse resp, Announcement existing)
            throws ServletException, IOException {
        String title = param(req, "title");
        String content = param(req, "content");

        Validator v = new Validator()
                .required("title", title, "Judul").max("title", title, 150, "Judul")
                .required("content", content, "Isi pengumuman");

        if (v.fails()) {
            req.setAttribute("errors", v.errors());
            req.setAttribute("announcement", existing);
            keepOld(req);
            view(req, resp, "announcements/form", NAV);
            return;
        }

        if (existing == null) {
            dao.create(currentUserId(req), title, content);
            flash(req, "Pengumuman berhasil ditambahkan.");
        } else {
            dao.update(existing.getId(), title, content);
            flash(req, "Pengumuman berhasil diperbarui.");
        }
        redirect(req, resp, "/announcements");
    }
}
