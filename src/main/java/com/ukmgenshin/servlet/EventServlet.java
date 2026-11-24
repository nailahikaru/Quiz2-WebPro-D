package com.ukmgenshin.servlet;

import com.ukmgenshin.dao.EventDao;
import com.ukmgenshin.model.Event;
import com.ukmgenshin.model.EventDivision;
import com.ukmgenshin.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/** CRUD Event kepanitiaan + kelola divisi (EventController). Daftar dan detail event bisa dilihat tamu. */
@WebServlet("/events/*")
public class EventServlet extends BaseServlet {

    private static final String NAV = "events";
    private static final String DEFAULT_DIVISIONS = "Acara\nHumas\nDokumentasi\nPerlengkapan\nKonsumsi\nDesain";

    private final EventDao dao = new EventDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (action(req)) {
            case "":
                req.setAttribute("events", dao.findAll());
                view(req, resp, "events/index", NAV);
                break;
            case "show": {
                Event e = dao.find(longParam(req, "id"));
                if (e == null) {
                    notFound(resp);
                    return;
                }
                showEvent(req, resp, e);
                break;
            }
            case "create":
                req.setAttribute("divisionText", DEFAULT_DIVISIONS);
                view(req, resp, "events/form", NAV);
                break;
            case "edit": {
                Event e = dao.find(longParam(req, "id"));
                if (e == null) {
                    notFound(resp);
                    return;
                }
                req.setAttribute("event", e);
                req.setAttribute("divisionText", e.getDivisions().stream()
                        .map(EventDivision::getName).collect(Collectors.joining("\n")));
                view(req, resp, "events/form", NAV);
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
                Event e = dao.find(longParam(req, "id"));
                if (e == null) {
                    notFound(resp);
                    return;
                }
                save(req, resp, e);
                break;
            }
            case "delete": {
                Event e = dao.find(longParam(req, "id"));
                if (e == null) {
                    notFound(resp);
                    return;
                }
                dao.delete(e.getId());
                flash(req, "Event berhasil dihapus.");
                redirect(req, resp, "/events");
                break;
            }
            case "division-add":
                divisionAdd(req, resp);
                break;
            case "division-rename":
                divisionRename(req, resp);
                break;
            case "division-delete":
                divisionDelete(req, resp);
                break;
            default:
                notFound(resp);
        }
    }

    // ---------- halaman detail ----------

    private void showEvent(HttpServletRequest req, HttpServletResponse resp, Event e)
            throws ServletException, IOException {
        req.setAttribute("event", e);
        req.setAttribute("registrations", dao.registrations(e.getId()));
        view(req, resp, "events/show", NAV);
    }

    // ---------- simpan event ----------

    private void save(HttpServletRequest req, HttpServletResponse resp, Event existing)
            throws ServletException, IOException {
        Event e = existing == null ? new Event() : existing;
        Validator v = new Validator();

        String name = param(req, "name");
        String description = param(req, "description");
        String location = param(req, "location");
        v.required("name", name, "Nama event").max("name", name, 150, "Nama event");
        v.max("location", location, 150, "Lokasi");

        LocalDate start = dateParam(req, "start_date");
        LocalDate end = dateParam(req, "end_date");
        LocalDate deadline = dateParam(req, "registration_deadline");
        if (start == null) {
            v.add("start_date", "Tanggal mulai wajib diisi.");
        }
        if (end == null) {
            v.add("end_date", "Tanggal selesai wajib diisi.");
        } else if (start != null && end.isBefore(start)) {
            v.add("end_date", "Tanggal selesai tidak boleh sebelum tanggal mulai.");
        }
        if (deadline == null) {
            v.add("registration_deadline", "Batas pendaftaran wajib diisi.");
        } else if (start != null && deadline.isAfter(start)) {
            v.add("registration_deadline", "Batas pendaftaran tidak boleh setelah tanggal mulai.");
        }

        int quota = 0;
        try {
            quota = Integer.parseInt(param(req, "quota"));
        } catch (NumberFormatException ignored) {
            // ditangani di bawah
        }
        if (quota < 1) {
            v.add("quota", "Kuota minimal 1.");
        }

        List<String> divisions = parseDivisions(req.getParameter("divisions"));
        if (!divisionsValid(divisions)) {
            v.add("divisions", "Isi minimal satu divisi, maksimal 50 karakter per nama.");
        }

        if (v.fails()) {
            req.setAttribute("errors", v.errors());
            req.setAttribute("event", existing);
            req.setAttribute("divisionText", req.getParameter("divisions"));
            keepOld(req);
            view(req, resp, "events/form", NAV);
            return;
        }

        e.setName(name);
        e.setDescription(description.isEmpty() ? null : description);
        e.setLocation(location.isEmpty() ? null : location);
        e.setStartDate(start);
        e.setEndDate(end);
        e.setRegistrationDeadline(deadline);
        e.setQuota(quota);

        if (existing == null) {
            e.setUserId(currentUserId(req));
            dao.create(e, divisions);
            flash(req, "Event berhasil ditambahkan.");
            redirect(req, resp, "/events");
        } else {
            dao.update(e, divisions);
            flash(req, "Event berhasil diperbarui.");
            redirect(req, resp, "/events/show?id=" + e.getId());
        }
    }

    /** Satu divisi per baris, spasi dibuang, baris kosong dan duplikat dihapus. */
    private static List<String> parseDivisions(String text) {
        LinkedHashSet<String> names = new LinkedHashSet<>();
        if (text != null) {
            for (String line : text.split("\\R")) {
                String name = line.trim();
                if (!name.isEmpty()) {
                    names.add(name);
                }
            }
        }
        return new ArrayList<>(names);
    }

    private static boolean divisionsValid(List<String> names) {
        if (names.isEmpty()) {
            return false;
        }
        for (String name : names) {
            if (name.length() > 50) {
                return false;
            }
        }
        return true;
    }

    // ---------- kelola divisi satu per satu ----------

    private void divisionAdd(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Event e = dao.find(longParam(req, "event_id"));
        if (e == null) {
            notFound(resp);
            return;
        }
        String name = param(req, "name");
        Validator v = new Validator().required("division_name", name, "Nama divisi")
                .max("division_name", name, 50, "Nama divisi");
        if (!v.fails() && dao.divisionExists(e.getId(), name, 0)) {
            v.add("division_name", "Divisi itu sudah ada.");
        }
        if (v.fails()) {
            req.setAttribute("errors", v.errors());
            showEvent(req, resp, e);
            return;
        }
        dao.addDivision(e.getId(), name);
        flash(req, "Divisi berhasil ditambahkan.");
        redirect(req, resp, "/events/show?id=" + e.getId());
    }

    private void divisionRename(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        EventDivision d = dao.findDivision(longParam(req, "id"));
        if (d == null) {
            notFound(resp);
            return;
        }
        Event e = dao.find(d.getEventId());
        String name = param(req, "name");
        Validator v = new Validator().required("division_name", name, "Nama divisi")
                .max("division_name", name, 50, "Nama divisi");
        if (!v.fails() && dao.divisionExists(e.getId(), name, d.getId())) {
            v.add("division_name", "Divisi itu sudah ada.");
        }
        if (v.fails()) {
            req.setAttribute("errors", v.errors());
            showEvent(req, resp, e);
            return;
        }
        dao.renameDivision(d.getId(), name);
        flash(req, "Divisi berhasil diubah.");
        redirect(req, resp, "/events/show?id=" + e.getId());
    }

    private void divisionDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        EventDivision d = dao.findDivision(longParam(req, "id"));
        if (d == null) {
            notFound(resp);
            return;
        }
        Event e = dao.find(d.getEventId());
        if (e.getDivisions().size() <= 1) {
            Validator v = new Validator();
            v.add("division_name", "Event harus punya minimal satu divisi.");
            req.setAttribute("errors", v.errors());
            showEvent(req, resp, e);
            return;
        }
        dao.deleteDivision(d.getId());
        flash(req, "Divisi berhasil dihapus.");
        redirect(req, resp, "/events/show?id=" + e.getId());
    }
}
