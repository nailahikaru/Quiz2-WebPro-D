package com.ukmgenshin.util;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Fungsi-fungsi yang dipanggil dari JSP lewat EL (lihat WEB-INF/ukm.tld, prefix "u"). */
public final class Fmt {

    private Fmt() {
    }

    /** ${u:date(x, 'dd MMM yyyy')} - menerima LocalDate, LocalDateTime, atau LocalTime. */
    public static String date(Object value, String pattern) {
        if (value == null) {
            return "";
        }
        DateTimeFormatter f = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).format(f);
        }
        if (value instanceof LocalDate) {
            return ((LocalDate) value).format(f);
        }
        if (value instanceof LocalTime) {
            return ((LocalTime) value).format(f);
        }
        return String.valueOf(value);
    }

    /** ${u:iso(x)} - nilai untuk input type="date" (yyyy-MM-dd). */
    public static String iso(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    /** ${u:rupiah(1500000)} -> 1.500.000 */
    public static String rupiah(long amount) {
        NumberFormat nf = NumberFormat.getIntegerInstance(Locale.forLanguageTag("id-ID"));
        return nf.format(amount);
    }

    /** Escape HTML lalu ubah baris baru menjadi <br> (padanan nl2br(e($x))). */
    public static String nl2br(String text) {
        if (text == null) {
            return "";
        }
        return escape(text).replace("\r\n", "<br>").replace("\r", "<br>").replace("\n", "<br>");
    }

    /** Huruf pertama (kapital) untuk avatar. */
    public static String initial(String name) {
        if (name == null || name.isBlank()) {
            return "A";
        }
        return name.trim().substring(0, 1).toUpperCase(Locale.ROOT);
    }

    public static String ucfirst(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return text.substring(0, 1).toUpperCase(Locale.ROOT) + text.substring(1);
    }

    private static String escape(String s) {
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '&':
                    sb.append("&amp;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&#39;");
                    break;
                default:
                    sb.append(ch);
            }
        }
        return sb.toString();
    }
}
