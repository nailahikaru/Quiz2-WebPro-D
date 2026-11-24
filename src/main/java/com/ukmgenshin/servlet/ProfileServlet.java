package com.ukmgenshin.servlet;

import com.ukmgenshin.dao.UserDao;
import com.ukmgenshin.model.User;
import com.ukmgenshin.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Profil saya: lihat dan ubah nama/email (ProfileController). */
@WebServlet("/profile")
public class ProfileServlet extends BaseServlet {

    private final UserDao users = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        view(req, resp, "profile", "profile");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User me = currentUser(req);
        String name = param(req, "name");
        String email = param(req, "email");
        String password = req.getParameter("password");

        Validator v = new Validator()
                .required("name", name, "Nama").max("name", name, 255, "Nama")
                .required("email", email, "Email").email("email", email, "Email").max("email", email, 255, "Email");
        if (!v.fails() && users.emailTaken(email, me.getId())) {
            v.add("email", "Email ini sudah dipakai akun lain.");
        }
        boolean changePassword = password != null && !password.isEmpty();
        if (changePassword) {
            v.min("password", password, 8, "Password");
            if (!password.equals(req.getParameter("password_confirmation"))) {
                v.add("password", "Konfirmasi password tidak cocok.");
            }
        }

        if (v.fails()) {
            req.setAttribute("errors", v.errors());
            keepOld(req);
            view(req, resp, "profile", "profile");
            return;
        }

        users.update(me.getId(), name, email);
        if (changePassword) {
            users.updatePassword(me.getId(), com.ukmgenshin.util.PasswordUtil.hash(password));
        }
        me.setName(name);
        me.setEmail(email);
        flash(req, "Profil berhasil diperbarui.");
        redirect(req, resp, "/profile");
    }
}
