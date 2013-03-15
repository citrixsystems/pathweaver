package org.daylight.pathweaver.plugin.exception;

public class RollbackException extends PluginException {
	private static final long serialVersionUID = -1850300663038738768L;

    public RollbackException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public RollbackException(String message) {
        super(message);
    }

    public RollbackException(Throwable throwable) {
        super(throwable);
    }
}
