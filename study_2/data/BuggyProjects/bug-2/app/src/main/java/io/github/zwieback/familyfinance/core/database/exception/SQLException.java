package io.github.zwieback.familyfinance.core.database.exception;

public class SQLException extends RuntimeException {

    private static final long serialVersionUID = 122863976549240999L;

    public SQLException(String message, Throwable cause) {
        super(message, cause);
    }
}
