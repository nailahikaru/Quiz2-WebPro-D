-- UKM Genshin (JSP) - skema database MySQL / MariaDB
-- Jalankan sekali:  mysql -u root -p < database/schema.sql

CREATE DATABASE IF NOT EXISTS ukm_genshin
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ukm_genshin;

CREATE TABLE IF NOT EXISTS users (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(255) NOT NULL,
  email       VARCHAR(255) NOT NULL,
  password    VARCHAR(255) NOT NULL,
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_users_email (email)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS announcements (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id     BIGINT UNSIGNED NULL,
  title       VARCHAR(255) NOT NULL,
  content     TEXT NOT NULL,
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_ann_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS portfolios (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id     BIGINT UNSIGNED NULL,
  title       VARCHAR(255) NOT NULL,
  description TEXT NULL,
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_port_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS portfolio_images (
  id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  portfolio_id  BIGINT UNSIGNED NOT NULL,
  path          VARCHAR(255) NOT NULL,
  created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_img_port FOREIGN KEY (portfolio_id) REFERENCES portfolios(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS events (
  id                     BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id                BIGINT UNSIGNED NULL,
  name                   VARCHAR(255) NOT NULL,
  description            TEXT NULL,
  location               VARCHAR(255) NULL,
  start_date             DATE NOT NULL,
  end_date               DATE NOT NULL,
  registration_deadline  DATE NOT NULL,
  quota                  INT UNSIGNED NOT NULL,
  created_at             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_event_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS event_divisions (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  event_id    BIGINT UNSIGNED NOT NULL,
  name        VARCHAR(50) NOT NULL,
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_div_event_name (event_id, name),
  CONSTRAINT fk_div_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS event_registrations (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  event_id    BIGINT UNSIGNED NOT NULL,
  user_id     BIGINT UNSIGNED NULL,
  name        VARCHAR(255) NOT NULL,
  nrp         VARCHAR(10) NOT NULL,
  division    VARCHAR(255) NOT NULL,
  reason      TEXT NOT NULL,
  status      VARCHAR(20) NOT NULL DEFAULT 'menunggu',
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_reg_event_nrp (event_id, nrp),
  CONSTRAINT fk_reg_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
  CONSTRAINT fk_reg_user  FOREIGN KEY (user_id)  REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Catatan: di versi Laravel, jadwal & kas ikut terhapus saat user dihapus (CASCADE).
-- Di sini dibuat SET NULL supaya catatan kas tidak hilang ketika seorang anggota dihapus.
CREATE TABLE IF NOT EXISTS schedules (
  id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id        BIGINT UNSIGNED NULL,
  event_name     VARCHAR(255) NOT NULL,
  event_date     DATE NOT NULL,
  event_time     TIME NOT NULL,
  event_location VARCHAR(255) NOT NULL,
  event_desc     TEXT NULL,
  created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_sch_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS presences (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  sch_id      BIGINT UNSIGNED NOT NULL,
  user_id     BIGINT UNSIGNED NOT NULL,
  status      VARCHAR(10) NOT NULL DEFAULT 'alfa',
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_presence (sch_id, user_id),
  CONSTRAINT fk_pres_sch  FOREIGN KEY (sch_id)  REFERENCES schedules(id) ON DELETE CASCADE,
  CONSTRAINT fk_pres_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS cashes (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id      BIGINT UNSIGNED NULL,
  type         VARCHAR(10) NOT NULL,           -- 'income' | 'expense'
  amount       BIGINT NOT NULL,
  description  VARCHAR(255) NOT NULL,
  tx_date      DATE NOT NULL,
  created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_cash_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;
