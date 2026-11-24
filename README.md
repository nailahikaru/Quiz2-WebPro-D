# UKM Genshin — versi JSP

Port dari website Laravel (`Midterm-WebProD`) ke **JSP + Servlet + JDBC (MySQL)**, dengan pola MVC:
`Servlet` = controller, `dao/` = akses database, `model/` = POJO, `WEB-INF/views/*.jsp` = view.

## Modul CRUD

| # | Modul | Create | Read | Update | Delete |
|---|-------|:--:|:--:|:--:|:--:|
| 1 | Anggota (users) | /users/create, /register | /users | /users/edit, /profile | ✔ |
| 2 | Pengumuman | ✔ | ✔ (publik) | ✔ | ✔ |
| 3 | Portofolio (+ upload 1-5 gambar) | ✔ | ✔ (publik) | ✔ (judul, deskripsi, tambah gambar) | ✔ (portofolio / per gambar) |
| 4 | Event | ✔ | ✔ (publik) | ✔ | ✔ |
| 5 | Divisi event | ✔ | ✔ | ✔ (ganti nama) | ✔ |
| 6 | Pendaftaran event | ✔ (login) | ✔ (publik) | ✔ (data + terima/tolak) | ✔ |
| 7 | Jadwal | ✔ | ✔ | ✔ | ✔ |
| 8 | Kas | ✔ | ✔ (+ saldo) | ✔ | ✔ |
| 9 | Presensi | ✔ | ✔ | ✔ | ✔ (reset tanda) |

## Cara menjalankan

Syarat: **JDK 17+**, **Maven**, **MySQL/MariaDB** (XAMPP juga bisa), dan **Apache Tomcat 10.1+**
(proyek memakai namespace `jakarta.*`; Tomcat 9 dan yang lebih lama tidak cocok, lihat catatan di bawah).

1. Buat database dan tabel:
   ```bash
   mysql -u root -p < database/schema.sql
   mysql -u root -p ukm_genshin < database/seed.sql     # opsional: data contoh + akun admin@ukm.test / password123
   ```
2. Atur koneksi database di `src/main/resources/db.properties`, atau lewat environment variable
   `DB_URL`, `DB_USER`, `DB_PASS`.
3. Build WAR:
   ```bash
   mvn clean package
   ```
4. Salin `target/ukm-genshin.war` ke folder `webapps/` Tomcat, jalankan Tomcat, lalu buka
   <http://localhost:8080/ukm-genshin/>.

Di IntelliJ / Eclipse / NetBeans: buka sebagai proyek Maven, tambahkan konfigurasi server **Tomcat 10.1**, deploy artifact `ukm-genshin:war exploded`.

Gambar portofolio disimpan di `~/ukm-genshin-uploads` (ubah dengan environment variable `UPLOAD_DIR`),
supaya tidak hilang saat WAR di-deploy ulang. Folder itu disajikan lewat `/uploads/...`.

## Pemetaan Laravel -> JSP

| Laravel | JSP |
|---|---|
| `routes/web.php` | `@WebServlet` di tiap servlet + `AuthFilter` (pengganti middleware `auth`) |
| Controller | `servlet/*Servlet.java` (aksi dibaca dari path: `/create`, `/edit`, `/store`, `/update`, `/delete`) |
| Eloquent model | `model/*.java` + `dao/*Dao.java` (JDBC `PreparedStatement`) |
| Blade `@extends` / `<x-layout>` | tag file `WEB-INF/tags/layout.tag` (`<t:layout>`) |
| `@csrf` | `<t:csrf/>` + `CsrfFilter` |
| `old()`, `$errors` | atribut request `old` dan `errors` (`Validator`) |
| `session('success')` | flash message di `BaseServlet.flash()` |
| `Hash::make` | BCrypt (jBCrypt) |
| `{{ $x }}` | `<c:out value="${x}"/>` (wajib, supaya aman dari XSS) |

## Perbedaan dari versi Laravel

- Jadwal dan kas sekarang punya fitur **Ubah** (di Laravel hanya tambah dan hapus).
- Ditambah halaman **Anggota** (`/users`), kelola **divisi** satu per satu, ubah/hapus **pendaftar**, hapus **tanda presensi**, dan hapus/tambah gambar portofolio.
- Pada tabel `schedules` dan `cashes`, relasi ke user dibuat `ON DELETE SET NULL` (di Laravel `CASCADE`), supaya catatan kas tidak ikut hilang saat anggota dihapus.
- Perbaikan path CSS: Laravel memanggil `assets/css/schedule.css` yang tidak ada; di sini memakai `assets/schedule.css`, dan path gambar latar dibuat relatif agar jalan di context path `/ukm-genshin`.

## Kalau memakai Tomcat 9 atau lebih lama (javax)

1. Ganti dependency di `pom.xml`: `javax.servlet:javax.servlet-api:4.0.1` (provided), `javax.servlet.jsp:javax.servlet.jsp-api:2.3.3` (provided), `javax.servlet:jstl:1.2`.
2. Ganti semua `jakarta.servlet` menjadi `javax.servlet` di kode Java (`sed -i 's/jakarta\.servlet/javax.servlet/g'`), dan di `web.xml` pakai skema `javax` (web-app 4.0).
3. Ganti URI JSTL di `WEB-INF/jspf/taglibs.jspf` dan `layout.tag`/`navbar.tag`/`alerts.tag`:
   `jakarta.tags.core` -> `http://java.sun.com/jsp/jstl/core`, `jakarta.tags.functions` -> `http://java.sun.com/jsp/jstl/functions`.
4. Di `ukm.tld` ganti namespace ke `http://java.sun.com/xml/ns/javaee` dengan `version="2.1"`.
