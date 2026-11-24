-- Data contoh (opsional). Jalankan setelah schema.sql:
--   mysql -u root -p ukm_genshin < database/seed.sql
-- Akun demo:  admin@ukm.test  /  password123
USE ukm_genshin;

INSERT INTO users (name, email, password) VALUES
  ('Admin UKM', 'admin@ukm.test', '$2a$10$0pgRrYwTUUNoVgJZspoHiuLZEK07v3VnDvX.TeoFKETO5X.adpTxu');

INSERT INTO announcements (user_id, title, content) VALUES
  (1, 'Selamat datang di UKM Genshin', 'Website UKM sekarang berjalan dengan JSP.\nSilakan cek jadwal dan event terbaru.');

INSERT INTO events (user_id, name, description, location, start_date, end_date, registration_deadline, quota) VALUES
  (1, 'Genshin Fest', 'Acara tahunan UKM Genshin.', 'Aula Akademiya', '2026-12-20', '2026-12-21', '2026-12-10', 20);

INSERT INTO event_divisions (event_id, name) VALUES
  (1, 'Acara'), (1, 'Humas'), (1, 'Dokumentasi'), (1, 'Perlengkapan'), (1, 'Konsumsi'), (1, 'Desain');

INSERT INTO schedules (user_id, event_name, event_date, event_time, event_location, event_desc) VALUES
  (1, 'Rapat Mingguan', '2026-12-01', '16:00:00', 'Ruang UKM', 'Evaluasi kegiatan pekan ini.');

INSERT INTO cashes (user_id, type, amount, description, tx_date) VALUES
  (1, 'income', 100000, 'Kas anggota', '2026-11-20'),
  (1, 'expense', 35000, 'Konsumsi rapat', '2026-11-21');
