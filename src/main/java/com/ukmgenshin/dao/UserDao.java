package com.ukmgenshin.dao;

import com.ukmgenshin.model.User;
import com.ukmgenshin.util.Jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDao {

    private static User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setCreatedAt(Jdbc.dateTime(rs, "created_at"));
        return u;
    }

    public List<User> findAll() {
        return Jdbc.list("SELECT * FROM users ORDER BY name", UserDao::map);
    }

    public User find(Long id) {
        if (id == null) {
            return null;
        }
        return Jdbc.one("SELECT * FROM users WHERE id = ?", UserDao::map, id);
    }

    public User findByEmail(String email) {
        return Jdbc.one("SELECT * FROM users WHERE email = ?", UserDao::map, email);
    }

    /** True jika email dipakai user lain (excludeId = id yang diabaikan, boleh 0). */
    public boolean emailTaken(String email, long excludeId) {
        return Jdbc.count("SELECT COUNT(*) FROM users WHERE email = ? AND id <> ?", email, excludeId) > 0;
    }

    public long create(String name, String email, String passwordHash) {
        return Jdbc.insert("INSERT INTO users (name, email, password) VALUES (?, ?, ?)",
                name, email, passwordHash);
    }

    public void update(long id, String name, String email) {
        Jdbc.update("UPDATE users SET name = ?, email = ? WHERE id = ?", name, email, id);
    }

    public void updatePassword(long id, String passwordHash) {
        Jdbc.update("UPDATE users SET password = ? WHERE id = ?", passwordHash, id);
    }

    public void delete(long id) {
        Jdbc.update("DELETE FROM users WHERE id = ?", id);
    }
}
