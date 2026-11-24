package com.ukmgenshin.servlet;

import com.ukmgenshin.dao.UserDao;
import com.ukmgenshin.model.User;
import com.ukmgenshin.util.PasswordUtil;
import com.ukmgenshin.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Kelola data anggota: daftar, tambah, ubah, hapus. */
@WebServlet("/users/*")
public class UserServlet extends BaseServlet {

    private final UserDao users = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (action(req)) {
            case "":
                req.setAttribute("users", users.findAll());
                view(req, resp, "users/index", "users");
                break;
            case "create":
                view(req, resp, "users/form", "users");
                break;
            case "edit": {
                User u = users.find(longParam(req, "id"));
                if (u == null) {
                    notFound(resp);
                    return;
                }
                req.setAttribute("member", u);
                view(req, resp, "users/form", "users");
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
                User u = users.find(longParam(req, "id"));
                if (u == null) {
                    notFound(resp);
                    return;
                }
                save(req, resp, u);
                break;
            }
            case "delete":
                delete(req, resp);
                break;
            default:
                notFound(resp);
        }
    }

    private void save(HttpServletRequest req, HttpServletResponse resp, User existing)
            throws ServletException, IOException {
        String name = param(req, "name");
        String email = param(req, "email");
        String password = req.getParameter("password");
        boolean hasPassword = password != null && !password.isEmpty();

        Validator v = new Validator()
                .required("name", name, "Nama").max("name", name, 255, "Nama")
                .required("email", email, "Email").email("email", email, "Email").max("email", email, 255, "Email");
        if (existing == null || hasPassword) {
            v.required("password", password, "Password").min("password", password, 8, "Password");
        }
        if (!v.fails() && users.emailTaken(email, existing == null ? 0 : existing.getId())) {
            v.add("email", "Email ini sudah terdaftar.");
        }

        if (v.fails()) {
            req.setAttribute("errors", v.errors());
            req.setAttribute("member", existing);
            keepOld(req);
            view(req, resp, "users/form", "users");
            return;
        }

        if (existing == null) {
            users.create(name, email, PasswordUtil.hash(password));
            flash(req, "Anggota berhasil ditambahkan.");
        } else {
            users.update(existing.getId(), name, email);
            if (hasPassword) {
                users.updatePassword(existing.getId(), PasswordUtil.hash(password));
            }
            User me = currentUser(req);
            if (me != null && me.getId() == existing.getId()) {
                me.setName(name);
                me.setEmail(email);
            }
            flash(req, "Data anggota berhasil diperbarui.");
        }
        redirect(req, resp, "/users");
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = longParam(req, "id");
        User target = users.find(id);
        if (target == null) {
            notFound(resp);
            return;
        }
        if (target.getId() == currentUser(req).getId()) {
            flash(req, "Kamu tidak bisa menghapus akunmu sendiri.");
        } else {
            users.delete(target.getId());
            flash(req, "Anggota berhasil dihapus.");
        }
        redirect(req, resp, "/users");
    }
}
