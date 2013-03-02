package org.daylight.pathweaver.api.validation.util.IPString;

public class IPStringConversionException extends Exception {

    public IPStringConversionException() {
    }

    public IPStringConversionException(String msg) {
        super(msg);
    }

    public IPStringConversionException(String msg, Exception innerExcept) {
        super(msg, innerExcept);
    }
}
