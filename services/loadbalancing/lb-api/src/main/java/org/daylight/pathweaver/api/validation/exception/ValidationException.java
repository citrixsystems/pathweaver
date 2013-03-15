package org.daylight.pathweaver.api.validation.exception;

public class ValidationException extends RuntimeException {

    private String message;
    private Throwable throwable;

    public ValidationException(String message, Throwable thrwbl) {
        this.message = message;
        this.throwable = thrwbl;
    }

    public ValidationException(String message) {
        this.message = getMessage();
    }
}
