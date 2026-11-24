package com.ukmgenshin.servlet;

import com.ukmgenshin.dao.EventDao;
import com.ukmgenshin.model.Event;
import com.ukmgenshin.model.EventDivision;
import com.ukmgenshin.model.EventRegistration;
import com.ukmgenshin.util.DataAccessException;
import com.ukmgenshin.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

/** Pendaftaran panitia event: daftar, ubah status (terima/tolak), ubah data, hapus. */
@WebServlet("/registrations/*")
public class EventRegistrationServlet extends BaseServlet {

    private static final Set<String> STATUSES = Set.of("menunggu", "diterima", "ditolak");

    private final EventDao dao = new EventDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        notFound(resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (action(req)) {
            case "store":
                store(req, resp);
                break;
            case "status":
                status(req, resp);
                break;
            case "update":
                update(req, resp);
                break;
            case "delete":
                delete(req, resp);
                break;
            default:
                notFound(resp);
        }
    }

    private void store(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Event event = dao.find(longParam(req, "event_id"));
        if (event == null) {
            notFound(resp);
            return;
        }

        String name = param(req, "name");
        String nrp = param(req, "nrp");
        String division = param(req, "division");
        String reason = param(req, "reason");

        Validator v = new Validator();
        if (!event.isRegistrationOpen()) {
            v.add("name", "Pendaftaran untuk event ini sudah ditutup.");
        } else if (event.getSeatsLeft() < 1) {
            v.add("name", "Kuota event ini sudah penuh.");
        }
        v.required("name", name, "Nama lengkap").max("name", name, 100, "Nama lengkap");
        if (nrp.isEmpty()) {
            v.add("nrp", "NRP wajib diisi.");
        } else if (!nrp.matches("\\d{10}")) {
            v.add("nrp", "NRP harus terdiri dari 10 digit angka.");
        } else if (dao.nrpRegistered(event.getId(), nrp)) {
            v.add("nrp", "NRP ini sudah terdaftar di event ini.");
        }
        if (division.isEmpty()) {
            v.add("division", "Divisi wajib dipilih.");
        } else if (!divisionOffered(event, division)) {
            v.add("division", "Divisi yang dipilih tidak valid.");
        }
        v.required("reason", reason, "Alasan bergabung");

        if (!v.fails()) {
            try {
                dao.createRegistration(event.getId(), currentUserId(req), name, nrp, division, reason);
                flash(req, "Pendaftaran berhasil dikirim.");
                redirect(req, resp, "/events/show?id=" + event.getId());
                return;
            } catch (DataAccessException e) {
                v.add("nrp", "NRP ini sudah terdaftar di event ini.");
            }
        }
        req.setAttribute("errors", v.errors());
        keepOld(req);
        req.setAttribute("event", event);
        req.setAttribute("registrations", dao.registrations(event.getId()));
        view(req, resp, "events/show", "events");
    }

    private void status(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        EventRegistration r = dao.findRegistration(longParam(req, "id"));
        String status = param(req, "status");
        if (r == null || !STATUSES.contains(status)) {
            notFound(resp);
            return;
        }
        dao.updateRegistrationStatus(r.getId(), status);
        flash(req, "Status pendaftaran diperbarui.");
        redirect(req, resp, "/events/show?id=" + r.getEventId());
    }

    private void update(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EventRegistration r = dao.findRegistration(longParam(req, "id"));
        if (r == null) {
            notFound(resp);
            return;
        }
        Event event = dao.find(r.getEventId());
        String name = param(req, "name");
        String division = param(req, "division");
        String reason = param(req, "reason");

        Validator v = new Validator()
                .required("name", name, "Nama lengkap").max("name", name, 100, "Nama lengkap")
                .required("reason", reason, "Alasan bergabung");
        if (!divisionOffered(event, division)) {
            v.add("division", "Divisi yang dipilih tidak valid.");
        }
        if (v.fails()) {
            req.setAttribute("errors", v.errors());
            req.setAttribute("event", event);
            req.setAttribute("registrations", dao.registrations(event.getId()));
            view(req, resp, "events/show", "events");
            return;
        }
        dao.updateRegistration(r.getId(), name, division, reason);
        flash(req, "Data pendaftar diperbarui.");
        redirect(req, resp, "/events/show?id=" + r.getEventId());
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        EventRegistration r = dao.findRegistration(longParam(req, "id"));
        if (r == null) {
            notFound(resp);
            return;
        }
        dao.deleteRegistration(r.getId());
        flash(req, "Pendaftar berhasil dihapus.");
        redirect(req, resp, "/events/show?id=" + r.getEventId());
    }

    private static boolean divisionOffered(Event event, String division) {
        for (EventDivision d : event.getDivisions()) {
            if (d.getName().equals(division)) {
                return true;
            }
        }
        return false;
    }
}
