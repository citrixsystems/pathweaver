package org.daylight.pathweaver.plugin.exception;

public class BadRequestException extends PluginException {
    private static final long serialVersionUID = -1197590882399930192L;

    public BadRequestException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(Throwable throwable) {
        super(throwable);
    }
}
