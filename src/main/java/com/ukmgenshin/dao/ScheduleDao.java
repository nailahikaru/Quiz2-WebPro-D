package com.ukmgenshin.dao;

import com.ukmgenshin.model.Schedule;
import com.ukmgenshin.util.Jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ScheduleDao {

    private static final String SELECT =
            "SELECT s.*, u.name AS creator_name FROM schedules s LEFT JOIN users u ON u.id = s.user_id ";

    private static Schedule map(ResultSet rs) throws SQLException {
        Schedule s = new Schedule();
        s.setId(rs.getLong("id"));
        s.setUserId(Jdbc.nullableLong(rs, "user_id"));
        s.setCreatorName(rs.getString("creator_name"));
        s.setEventName(rs.getString("event_name"));
        s.setEventDate(Jdbc.date(rs, "event_date"));
        s.setEventTime(Jdbc.time(rs, "event_time"));
        s.setEventLocation(rs.getString("event_location"));
        s.setEventDesc(rs.getString("event_desc"));
        s.setCreatedAt(Jdbc.dateTime(rs, "created_at"));
        return s;
    }

    public List<Schedule> findAll() {
        return Jdbc.list(SELECT + "ORDER BY s.event_date DESC, s.event_time DESC, s.id DESC", ScheduleDao::map);
    }

    public Schedule find(Long id) {
        if (id == null) {
            return null;
        }
        return Jdbc.one(SELECT + "WHERE s.id = ?", ScheduleDao::map, id);
    }

    public void create(Schedule s) {
        Jdbc.insert("INSERT INTO schedules (user_id, event_name, event_date, event_time, event_location, "
                + "event_desc) VALUES (?, ?, ?, ?, ?, ?)",
                s.getUserId(), s.getEventName(), s.getEventDate(), s.getEventTime(),
                s.getEventLocation(), s.getEventDesc());
    }

    public void update(Schedule s) {
        Jdbc.update("UPDATE schedules SET event_name = ?, event_date = ?, event_time = ?, event_location = ?, "
                + "event_desc = ? WHERE id = ?",
                s.getEventName(), s.getEventDate(), s.getEventTime(), s.getEventLocation(),
                s.getEventDesc(), s.getId());
    }

    public void delete(long id) {
        Jdbc.update("DELETE FROM schedules WHERE id = ?", id);
    }
}
