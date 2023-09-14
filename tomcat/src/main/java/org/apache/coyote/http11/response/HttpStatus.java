package org.apache.coyote.http11.response;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    ;

    private final int code;
    private final String message;

    HttpStatus(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
