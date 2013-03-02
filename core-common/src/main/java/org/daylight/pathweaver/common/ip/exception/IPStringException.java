package org.daylight.pathweaver.common.ip.exception;

public class IPStringException extends Exception {

    public IPStringException() {
    }

    public IPStringException(String msg) {
        super(msg);
    }

    public IPStringException(String msg, Exception innerExcept) {
        super(msg, innerExcept);
    }
}
