package com.ukmgenshin.servlet;

import com.ukmgenshin.dao.ScheduleDao;
import com.ukmgenshin.model.Schedule;
import com.ukmgenshin.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

/** CRUD Jadwal (SchedController). Semua aksi wajib login. */
@WebServlet("/schedule/*")
public class ScheduleServlet extends BaseServlet {

    private static final String NAV = "schedule";
    private final ScheduleDao dao = new ScheduleDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!action(req).isEmpty()) {
            notFound(resp);
            return;
        }
        showList(req, resp);
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("schedules", dao.findAll());
        view(req, resp, "schedule/index", NAV);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (action(req)) {
            case "store":
                save(req, resp, null);
                break;
            case "update": {
                Schedule s = dao.find(longParam(req, "id"));
                if (s == null) {
                    notFound(resp);
                    return;
                }
                save(req, resp, s);
                break;
            }
            case "delete": {
                Schedule s = dao.find(longParam(req, "id"));
                if (s == null) {
                    notFound(resp);
                    return;
                }
                dao.delete(s.getId());
                flash(req, "Schedule deleted successfully!");
                redirect(req, resp, "/schedule");
                break;
            }
            default:
                notFound(resp);
        }
    }

    private void save(HttpServletRequest req, HttpServletResponse resp, Schedule existing)
            throws ServletException, IOException {
        String name = param(req, "eventName");
        String location = param(req, "eventLocation");
        String desc = param(req, "eventDesc");
        LocalDate date = dateParam(req, "eventDate");
        LocalTime time = timeParam(req, "eventTime");

        Validator v = new Validator()
                .required("eventName", name, "Nama event").max("eventName", name, 255, "Nama event")
                .required("eventLocation", location, "Lokasi").max("eventLocation", location, 255, "Lokasi");
        if (date == null) {
            v.add("eventDate", "Tanggal wajib diisi dengan format yang valid.");
        }
        if (time == null) {
            v.add("eventTime", "Jam wajib diisi dengan format yang valid.");
        }

        if (v.fails()) {
            req.setAttribute("errors", v.errors());
            showList(req, resp);
            return;
        }

        Schedule s = existing == null ? new Schedule() : existing;
        s.setEventName(name);
        s.setEventDate(date);
        s.setEventTime(time);
        s.setEventLocation(location);
        s.setEventDesc(desc.isEmpty() ? null : desc);
        if (existing == null) {
            s.setUserId(currentUserId(req));
            dao.create(s);
            flash(req, "Schedule added successfully!");
        } else {
            dao.update(s);
            flash(req, "Schedule updated successfully!");
        }
        redirect(req, resp, "/schedule");
    }
}
