package com.ukmgenshin.servlet;

import com.ukmgenshin.dao.PresenceDao;
import com.ukmgenshin.dao.ScheduleDao;
import com.ukmgenshin.dao.UserDao;
import com.ukmgenshin.model.Schedule;
import com.ukmgenshin.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Presensi anggota per jadwal (PresenceController). Semua aksi wajib login. */
@WebServlet("/presence/*")
public class PresenceServlet extends BaseServlet {

    private static final String NAV = "presence";
    private static final Set<String> STATUSES = Set.of("present", "leave", "alfa");

    private final ScheduleDao schedules = new ScheduleDao();
    private final UserDao users = new UserDao();
    private final PresenceDao presences = new PresenceDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (action(req)) {
            case "":
            case "list":
                req.setAttribute("schedules", schedules.findAll());
                view(req, resp, "presence/list", NAV);
                break;
            case "view": {
                Schedule s = schedules.find(longParam(req, "id"));
                if (s == null) {
                    notFound(resp);
                    return;
                }
                req.setAttribute("schedule", s);
                req.setAttribute("users", users.findAll());
                req.setAttribute("presences", presences.statusByUser(s.getId()));
                view(req, resp, "presence/view", NAV);
                break;
            }
            default:
                notFound(resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Schedule s = schedules.find(longParam(req, "sch_id"));
        if (s == null) {
            notFound(resp);
            return;
        }
        switch (action(req)) {
            case "save": {
                List<User> all = users.findAll();
                Set<Long> validIds = new HashSet<>();
                for (User u : all) {
                    validIds.add(u.getId());
                }
                for (Map.Entry<String, String[]> e : req.getParameterMap().entrySet()) {
                    String key = e.getKey();
                    if (!key.startsWith("status[") || !key.endsWith("]") || e.getValue().length == 0) {
                        continue;
                    }
                    String status = e.getValue()[0];
                    try {
                        long userId = Long.parseLong(key.substring(7, key.length() - 1));
                        if (validIds.contains(userId) && STATUSES.contains(status)) {
                            presences.save(s.getId(), userId, status);
                        }
                    } catch (NumberFormatException ignored) {
                        // nama field aneh: lewati
                    }
                }
                flash(req, "Attendance saved successfully!");
                break;
            }
            case "reset": {
                Long userId = longParam(req, "user_id");
                if (userId == null) {
                    notFound(resp);
                    return;
                }
                presences.delete(s.getId(), userId);
                flash(req, "Attendance mark removed.");
                break;
            }
            default:
                notFound(resp);
                return;
        }
        redirect(req, resp, "/presence/view?id=" + s.getId());
    }
}
