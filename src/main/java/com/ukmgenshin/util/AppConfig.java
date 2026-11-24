package com.ukmgenshin.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/** Membaca konfigurasi dari environment variable, lalu db.properties. */
public final class AppConfig {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                PROPS.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Gagal membaca db.properties", e);
        }
    }

    private AppConfig() {
    }

    public static String dbUrl() {
        return pick("DB_URL", "db.url", "jdbc:mysql://localhost:3306/ukm_genshin");
    }

    public static String dbUser() {
        return pick("DB_USER", "db.user", "root");
    }

    public static String dbPassword() {
        return pick("DB_PASS", "db.password", "");
    }

    /** Folder tempat gambar portofolio disimpan (di luar folder WAR supaya tidak hilang saat redeploy). */
    public static Path uploadDir() {
        String dir = System.getenv("UPLOAD_DIR");
        if (dir == null || dir.isBlank()) {
            dir = PROPS.getProperty("upload.dir");
        }
        if (dir == null || dir.isBlank()) {
            dir = System.getProperty("user.home") + "/ukm-genshin-uploads";
        }
        return Paths.get(dir).toAbsolutePath().normalize();
    }

    private static String pick(String envName, String propName, String fallback) {
        String value = System.getenv(envName);
        if (value != null) {
            return value;
        }
        return PROPS.getProperty(propName, fallback);
    }
}
