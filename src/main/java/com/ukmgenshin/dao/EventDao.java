package com.ukmgenshin.dao;

import com.ukmgenshin.model.Event;
import com.ukmgenshin.model.EventDivision;
import com.ukmgenshin.model.EventRegistration;
import com.ukmgenshin.util.Jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EventDao {

    /** seats_taken = jumlah pendaftar yang tidak ditolak. */
    private static final String SELECT =
            "SELECT e.*, (SELECT COUNT(*) FROM event_registrations r "
            + "WHERE r.event_id = e.id AND r.status <> 'ditolak') AS seats_taken FROM events e ";

    private static Event map(ResultSet rs) throws SQLException {
        Event e = new Event();
        e.setId(rs.getLong("id"));
        e.setUserId(Jdbc.nullableLong(rs, "user_id"));
        e.setName(rs.getString("name"));
        e.setDescription(rs.getString("description"));
        e.setLocation(rs.getString("location"));
        e.setStartDate(Jdbc.date(rs, "start_date"));
        e.setEndDate(Jdbc.date(rs, "end_date"));
        e.setRegistrationDeadline(Jdbc.date(rs, "registration_deadline"));
        e.setQuota(rs.getInt("quota"));
        e.setSeatsTaken(rs.getInt("seats_taken"));
        e.setCreatedAt(Jdbc.dateTime(rs, "created_at"));
        return e;
    }

    private static EventDivision mapDivision(ResultSet rs) throws SQLException {
        EventDivision d = new EventDivision();
        d.setId(rs.getLong("id"));
        d.setEventId(rs.getLong("event_id"));
        d.setName(rs.getString("name"));
        return d;
    }

    private static EventRegistration mapRegistration(ResultSet rs) throws SQLException {
        EventRegistration r = new EventRegistration();
        r.setId(rs.getLong("id"));
        r.setEventId(rs.getLong("event_id"));
        r.setUserId(Jdbc.nullableLong(rs, "user_id"));
        r.setName(rs.getString("name"));
        r.setNrp(rs.getString("nrp"));
        r.setDivision(rs.getString("division"));
        r.setReason(rs.getString("reason"));
        r.setStatus(rs.getString("status"));
        r.setCreatedAt(Jdbc.dateTime(rs, "created_at"));
        return r;
    }

    // ---------- event ----------

    public List<Event> findAll() {
        return Jdbc.list(SELECT + "ORDER BY e.created_at DESC, e.id DESC", EventDao::map);
    }

    /** Event beserta daftar divisinya. */
    public Event find(Long id) {
        if (id == null) {
            return null;
        }
        Event e = Jdbc.one(SELECT + "WHERE e.id = ?", EventDao::map, id);
        if (e != null) {
            e.setDivisions(divisions(e.getId()));
        }
        return e;
    }

    public void create(Event e, List<String> divisionNames) {
        Jdbc.tx(c -> {
            long id = Jdbc.insert(c,
                    "INSERT INTO events (user_id, name, description, location, start_date, end_date, "
                    + "registration_deadline, quota) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    e.getUserId(), e.getName(), e.getDescription(), e.getLocation(),
                    e.getStartDate(), e.getEndDate(), e.getRegistrationDeadline(), e.getQuota());
            for (String name : divisionNames) {
                Jdbc.insert(c, "INSERT INTO event_divisions (event_id, name) VALUES (?, ?)", id, name);
            }
        });
    }

    /** Ubah event dan samakan daftar divisi dengan yang diinput (seperti syncDivisions di Laravel). */
    public void update(Event e, List<String> divisionNames) {
        Jdbc.tx(c -> {
            Jdbc.update(c,
                    "UPDATE events SET name = ?, description = ?, location = ?, start_date = ?, end_date = ?, "
                    + "registration_deadline = ?, quota = ? WHERE id = ?",
                    e.getName(), e.getDescription(), e.getLocation(), e.getStartDate(), e.getEndDate(),
                    e.getRegistrationDeadline(), e.getQuota(), e.getId());
            Jdbc.update(c, "DELETE FROM event_divisions WHERE event_id = ?", e.getId());
            for (String name : divisionNames) {
                Jdbc.insert(c, "INSERT INTO event_divisions (event_id, name) VALUES (?, ?)", e.getId(), name);
            }
        });
    }

    public void delete(long id) {
        Jdbc.update("DELETE FROM events WHERE id = ?", id);
    }

    // ---------- divisi ----------

    public List<EventDivision> divisions(long eventId) {
        return Jdbc.list("SELECT * FROM event_divisions WHERE event_id = ? ORDER BY id",
                EventDao::mapDivision, eventId);
    }

    public EventDivision findDivision(Long id) {
        if (id == null) {
            return null;
        }
        return Jdbc.one("SELECT * FROM event_divisions WHERE id = ?", EventDao::mapDivision, id);
    }

    public boolean divisionExists(long eventId, String name, long excludeId) {
        return Jdbc.count("SELECT COUNT(*) FROM event_divisions WHERE event_id = ? AND name = ? AND id <> ?",
                eventId, name, excludeId) > 0;
    }

    public void addDivision(long eventId, String name) {
        Jdbc.insert("INSERT INTO event_divisions (event_id, name) VALUES (?, ?)", eventId, name);
    }

    public void renameDivision(long id, String name) {
        Jdbc.update("UPDATE event_divisions SET name = ? WHERE id = ?", name, id);
    }

    public void deleteDivision(long id) {
        Jdbc.update("DELETE FROM event_divisions WHERE id = ?", id);
    }

    // ---------- pendaftaran ----------

    public List<EventRegistration> registrations(long eventId) {
        return Jdbc.list("SELECT * FROM event_registrations WHERE event_id = ? ORDER BY created_at DESC, id DESC",
                EventDao::mapRegistration, eventId);
    }

    public EventRegistration findRegistration(Long id) {
        if (id == null) {
            return null;
        }
        return Jdbc.one("SELECT * FROM event_registrations WHERE id = ?", EventDao::mapRegistration, id);
    }

    public boolean nrpRegistered(long eventId, String nrp) {
        return Jdbc.count("SELECT COUNT(*) FROM event_registrations WHERE event_id = ? AND nrp = ?",
                eventId, nrp) > 0;
    }

    public void createRegistration(long eventId, Long userId, String name, String nrp, String division,
                                   String reason) {
        Jdbc.insert("INSERT INTO event_registrations (event_id, user_id, name, nrp, division, reason, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, 'menunggu')", eventId, userId, name, nrp, division, reason);
    }

    public void updateRegistration(long id, String name, String division, String reason) {
        Jdbc.update("UPDATE event_registrations SET name = ?, division = ?, reason = ? WHERE id = ?",
                name, division, reason, id);
    }

    public void updateRegistrationStatus(long id, String status) {
        Jdbc.update("UPDATE event_registrations SET status = ? WHERE id = ?", status, id);
    }

    public void deleteRegistration(long id) {
        Jdbc.update("DELETE FROM event_registrations WHERE id = ?", id);
    }
}
