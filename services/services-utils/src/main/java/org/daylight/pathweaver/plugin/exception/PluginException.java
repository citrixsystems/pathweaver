package org.daylight.pathweaver.plugin.exception;

public class PluginException extends Exception {
    private static final long serialVersionUID = -1197590882399930192L;

    public PluginException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public PluginException(String message) {
        super(message);
    }

    public PluginException(Throwable throwable) {
        super(throwable);
    }
}
