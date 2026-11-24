package com.ukmgenshin.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/** Pengganti $request->validate() milik Laravel (pesan error dalam Bahasa Indonesia). */
public class Validator {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final Map<String, String> errors = new LinkedHashMap<>();

    public Validator required(String field, String value, String label) {
        if (value == null || value.isBlank()) {
            add(field, label + " wajib diisi.");
        }
        return this;
    }

    public Validator max(String field, String value, int max, String label) {
        if (value != null && value.length() > max && !errors.containsKey(field)) {
            add(field, label + " maksimal " + max + " karakter.");
        }
        return this;
    }

    public Validator min(String field, String value, int min, String label) {
        if (value != null && !value.isEmpty() && value.length() < min && !errors.containsKey(field)) {
            add(field, label + " minimal " + min + " karakter.");
        }
        return this;
    }

    public Validator email(String field, String value, String label) {
        if (value != null && !value.isBlank() && !EMAIL.matcher(value).matches() && !errors.containsKey(field)) {
            add(field, label + " harus berupa alamat email yang valid.");
        }
        return this;
    }

    public void add(String field, String message) {
        errors.putIfAbsent(field, message);
    }

    public boolean fails() {
        return !errors.isEmpty();
    }

    public Map<String, String> errors() {
        return errors;
    }
}
