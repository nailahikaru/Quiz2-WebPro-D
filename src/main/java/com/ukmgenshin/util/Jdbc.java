package com.ukmgenshin.util;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/** Helper JDBC kecil: semua query memakai PreparedStatement (aman dari SQL injection). */
public final class Jdbc {

    @FunctionalInterface
    public interface RowMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

    @FunctionalInterface
    public interface TxWork {
        void run(Connection c) throws SQLException;
    }

    private Jdbc() {
    }

    // ---------- tanpa Connection (ambil dari pool) ----------

    public static <T> List<T> list(String sql, RowMapper<T> mapper, Object... params) {
        try (Connection c = DBUtil.getConnection()) {
            return list(c, sql, mapper, params);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public static <T> T one(String sql, RowMapper<T> mapper, Object... params) {
        List<T> rows = list(sql, mapper, params);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public static long count(String sql, Object... params) {
        Long n = one(sql, rs -> rs.getLong(1), params);
        return n == null ? 0L : n;
    }

    public static int update(String sql, Object... params) {
        try (Connection c = DBUtil.getConnection()) {
            return update(c, sql, params);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /** INSERT dan kembalikan id hasil auto-increment. */
    public static long insert(String sql, Object... params) {
        try (Connection c = DBUtil.getConnection()) {
            return insert(c, sql, params);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /** Jalankan beberapa perintah dalam satu transaksi. */
    public static void tx(TxWork work) {
        try (Connection c = DBUtil.getConnection()) {
            boolean oldAutoCommit = c.getAutoCommit();
            c.setAutoCommit(false);
            try {
                work.run(c);
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(oldAutoCommit);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // ---------- dengan Connection (dipakai di dalam transaksi) ----------

    public static <T> List<T> list(Connection c, String sql, RowMapper<T> mapper, Object... params)
            throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> rows = new ArrayList<>();
                while (rs.next()) {
                    rows.add(mapper.map(rs));
                }
                return rows;
            }
        }
    }

    public static int update(Connection c, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            return ps.executeUpdate();
        }
    }

    public static long insert(Connection c, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, params);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getLong(1) : 0L;
            }
        }
    }

    // ---------- util ----------

    private static void bind(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object p = params[i];
            int idx = i + 1;
            if (p instanceof LocalDate) {
                ps.setDate(idx, Date.valueOf((LocalDate) p));
            } else if (p instanceof LocalTime) {
                ps.setTime(idx, Time.valueOf((LocalTime) p));
            } else if (p instanceof LocalDateTime) {
                ps.setTimestamp(idx, Timestamp.valueOf((LocalDateTime) p));
            } else {
                ps.setObject(idx, p);
            }
        }
    }

    public static LocalDate date(ResultSet rs, String column) throws SQLException {
        Date d = rs.getDate(column);
        return d == null ? null : d.toLocalDate();
    }

    public static LocalTime time(ResultSet rs, String column) throws SQLException {
        Time t = rs.getTime(column);
        return t == null ? null : t.toLocalTime();
    }

    public static LocalDateTime dateTime(ResultSet rs, String column) throws SQLException {
        Timestamp t = rs.getTimestamp(column);
        return t == null ? null : t.toLocalDateTime();
    }

    /** Kolom BIGINT yang boleh NULL. */
    public static Long nullableLong(ResultSet rs, String column) throws SQLException {
        long v = rs.getLong(column);
        return rs.wasNull() ? null : v;
    }
}
