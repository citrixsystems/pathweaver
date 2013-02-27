package org.daylight.pathweaver.service.domain.exception;

import org.daylight.pathweaver.service.domain.common.ErrorMessages;

public class UnprocessableEntityException extends PersistenceServiceException {
    private final String message;

    public UnprocessableEntityException(final String message) {
        this.message = message;
    }

    public UnprocessableEntityException(ErrorMessages messages) {
        this.message = messages.toString();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
