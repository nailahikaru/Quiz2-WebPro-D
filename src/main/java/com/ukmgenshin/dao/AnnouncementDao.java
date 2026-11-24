package com.ukmgenshin.dao;

import com.ukmgenshin.model.Announcement;
import com.ukmgenshin.util.Jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AnnouncementDao {

    private static final String SELECT =
            "SELECT a.*, u.name AS author_name FROM announcements a "
            + "LEFT JOIN users u ON u.id = a.user_id ";

    private static Announcement map(ResultSet rs) throws SQLException {
        Announcement a = new Announcement();
        a.setId(rs.getLong("id"));
        a.setUserId(Jdbc.nullableLong(rs, "user_id"));
        a.setAuthorName(rs.getString("author_name"));
        a.setTitle(rs.getString("title"));
        a.setContent(rs.getString("content"));
        a.setCreatedAt(Jdbc.dateTime(rs, "created_at"));
        return a;
    }

    public List<Announcement> findAll() {
        return Jdbc.list(SELECT + "ORDER BY a.created_at DESC, a.id DESC", AnnouncementDao::map);
    }

    public Announcement find(Long id) {
        if (id == null) {
            return null;
        }
        return Jdbc.one(SELECT + "WHERE a.id = ?", AnnouncementDao::map, id);
    }

    public long create(Long userId, String title, String content) {
        return Jdbc.insert("INSERT INTO announcements (user_id, title, content) VALUES (?, ?, ?)",
                userId, title, content);
    }

    public void update(long id, String title, String content) {
        Jdbc.update("UPDATE announcements SET title = ?, content = ? WHERE id = ?", title, content, id);
    }

    public void delete(long id) {
        Jdbc.update("DELETE FROM announcements WHERE id = ?", id);
    }
}
