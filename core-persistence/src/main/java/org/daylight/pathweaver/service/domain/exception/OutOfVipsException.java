package org.daylight.pathweaver.service.domain.exception;

import org.daylight.pathweaver.service.domain.common.ErrorMessages;

public class OutOfVipsException extends PersistenceServiceException {
    private final String message;

    public OutOfVipsException(final String message) {
        this.message = message;
    }

    public OutOfVipsException(ErrorMessages messages) {
        this.message = messages.toString();
    }


    public OutOfVipsException(ErrorMessages messages, Exception innerExcept) {
        super(messages.toString(), innerExcept);
        this.message = messages.toString();
    }
    @Override
    public String getMessage() {
        return message;
    }
}
