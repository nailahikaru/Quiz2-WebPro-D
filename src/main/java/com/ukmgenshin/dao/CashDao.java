package com.ukmgenshin.dao;

import com.ukmgenshin.model.Cash;
import com.ukmgenshin.util.Jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CashDao {

    private static final String SELECT =
            "SELECT c.*, u.name AS user_name FROM cashes c LEFT JOIN users u ON u.id = c.user_id ";

    private static Cash map(ResultSet rs) throws SQLException {
        Cash c = new Cash();
        c.setId(rs.getLong("id"));
        c.setUserId(Jdbc.nullableLong(rs, "user_id"));
        c.setUserName(rs.getString("user_name"));
        c.setType(rs.getString("type"));
        c.setAmount(rs.getLong("amount"));
        c.setDescription(rs.getString("description"));
        c.setTxDate(Jdbc.date(rs, "tx_date"));
        c.setCreatedAt(Jdbc.dateTime(rs, "created_at"));
        return c;
    }

    public List<Cash> findAll() {
        return Jdbc.list(SELECT + "ORDER BY c.tx_date DESC, c.id DESC", CashDao::map);
    }

    public Cash find(Long id) {
        if (id == null) {
            return null;
        }
        return Jdbc.one(SELECT + "WHERE c.id = ?", CashDao::map, id);
    }

    public long total(String type) {
        return Jdbc.count("SELECT COALESCE(SUM(amount), 0) FROM cashes WHERE type = ?", type);
    }

    public void create(Cash c) {
        Jdbc.insert("INSERT INTO cashes (user_id, type, amount, description, tx_date) VALUES (?, ?, ?, ?, ?)",
                c.getUserId(), c.getType(), c.getAmount(), c.getDescription(), c.getTxDate());
    }

    public void update(Cash c) {
        Jdbc.update("UPDATE cashes SET type = ?, amount = ?, description = ?, tx_date = ? WHERE id = ?",
                c.getType(), c.getAmount(), c.getDescription(), c.getTxDate(), c.getId());
    }

    public void delete(long id) {
        Jdbc.update("DELETE FROM cashes WHERE id = ?", id);
    }
}
