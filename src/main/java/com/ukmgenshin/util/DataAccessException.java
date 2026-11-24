package com.ukmgenshin.util;

/** Pembungkus SQLException agar tidak perlu try/catch di setiap DAO. */
public class DataAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DataAccessException(Throwable cause) {
        super(cause);
    }
}
