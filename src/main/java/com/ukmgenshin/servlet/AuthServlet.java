package com.ukmgenshin.servlet;

import com.ukmgenshin.dao.UserDao;
import com.ukmgenshin.filter.AuthFilter;
import com.ukmgenshin.model.User;
import com.ukmgenshin.util.Csrf;
import com.ukmgenshin.util.DataAccessException;
import com.ukmgenshin.util.PasswordUtil;
import com.ukmgenshin.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/** Login, register, dan logout (AuthController). */
@WebServlet({"/login", "/register", "/logout"})
public class AuthServlet extends BaseServlet {

    private final UserDao users = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if (path.equals("/logout")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        if (currentUser(req) != null) {
            redirect(req, resp, "/");
            return;
        }
        view(req, resp, path.equals("/login") ? "auth/login" : "auth/register", path.substring(1));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (req.getServletPath()) {
            case "/login":
                login(req, resp);
                break;
            case "/register":
                register(req, resp);
                break;
            default:
                logout(req, resp);
        }
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = param(req, "email");
        String password = req.getParameter("password");

        Validator v = new Validator()
                .required("email", email, "Email")
                .email("email", email, "Email")
                .required("password", password, "Password");

        if (!v.fails()) {
            User user = users.findByEmail(email);
            if (user != null && PasswordUtil.matches(password, user.getPassword())) {
                signIn(req, resp, user, req.getParameter("remember") != null);
                return;
            }
            v.add("email", "Email atau password salah.");
        }
        req.setAttribute("errors", v.errors());
        keepOld(req);
        view(req, resp, "auth/login", "login");
    }

    private void register(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = param(req, "name");
        String email = param(req, "email");
        String password = req.getParameter("password");
        String confirmation = req.getParameter("password_confirmation");

        Validator v = new Validator()
                .required("name", name, "Nama").max("name", name, 255, "Nama")
                .required("email", email, "Email").email("email", email, "Email").max("email", email, 255, "Email")
                .required("password", password, "Password").min("password", password, 8, "Password");
        if (password != null && !password.equals(confirmation)) {
            v.add("password", "Konfirmasi password tidak cocok.");
        }
        if (!v.fails() && users.emailTaken(email, 0)) {
            v.add("email", "Email ini sudah terdaftar.");
        }

        if (!v.fails()) {
            try {
                long id = users.create(name, email, PasswordUtil.hash(password));
                signIn(req, resp, users.find(id), false);
                return;
            } catch (DataAccessException e) {
                v.add("email", "Email ini sudah terdaftar.");
            }
        }
        req.setAttribute("errors", v.errors());
        keepOld(req);
        view(req, resp, "auth/register", "register");
    }

    private void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        redirect(req, resp, "/");
    }

    /** Buat sesi login baru (ganti session id agar aman dari session fixation). */
    private void signIn(HttpServletRequest req, HttpServletResponse resp, User user, boolean remember)
            throws IOException {
        HttpSession session = req.getSession(true);
        String intended = (String) session.getAttribute(AuthFilter.INTENDED);
        req.changeSessionId();

        User sessionUser = new User();
        sessionUser.setId(user.getId());
        sessionUser.setName(user.getName());
        sessionUser.setEmail(user.getEmail());
        session.setAttribute(AuthFilter.SESSION_USER, sessionUser);
        session.removeAttribute(AuthFilter.INTENDED);
        session.setMaxInactiveInterval(remember ? 7 * 24 * 3600 : 30 * 60);
        Csrf.regenerate(session);

        if (intended != null && intended.startsWith(req.getContextPath() + "/") && !intended.startsWith("//")) {
            resp.sendRedirect(intended);
        } else {
            redirect(req, resp, "/");
        }
    }
}
