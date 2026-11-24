package com.ukmgenshin.dao;

import com.ukmgenshin.util.Jdbc;

import java.util.HashMap;
import java.util.Map;

public class PresenceDao {

    /** Peta user_id -> status untuk satu jadwal. */
    public Map<Long, String> statusByUser(long scheduleId) {
        Map<Long, String> result = new HashMap<>();
        Jdbc.list("SELECT user_id, status FROM presences WHERE sch_id = ?", rs -> {
            result.put(rs.getLong("user_id"), rs.getString("status"));
            return Boolean.TRUE;
        }, scheduleId);
        return result;
    }

    /** Padanan updateOrCreate(): ubah jika sudah ada, kalau belum ada buat baru. */
    public void save(long scheduleId, long userId, String status) {
        Jdbc.tx(c -> {
            int changed = Jdbc.update(c, "UPDATE presences SET status = ? WHERE sch_id = ? AND user_id = ?",
                    status, scheduleId, userId);
            if (changed == 0) {
                Jdbc.insert(c, "INSERT INTO presences (sch_id, user_id, status) VALUES (?, ?, ?)",
                        scheduleId, userId, status);
            }
        });
    }

    /** Hapus tanda kehadiran seorang anggota pada satu jadwal (kembali ke "Not Marked"). */
    public void delete(long scheduleId, long userId) {
        Jdbc.update("DELETE FROM presences WHERE sch_id = ? AND user_id = ?", scheduleId, userId);
    }
}
