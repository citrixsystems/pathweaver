/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.daylight.pathweaver.common.ip.exception;

public class IPStringConversionException extends IPStringException {

    public IPStringConversionException() {
    }

    public IPStringConversionException(String msg) {
        super(msg);
    }

    public IPStringConversionException(String msg, Exception innerExcept) {
        super(msg, innerExcept);
    }
}
