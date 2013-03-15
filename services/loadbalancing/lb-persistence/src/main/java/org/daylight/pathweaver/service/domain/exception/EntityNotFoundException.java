package org.daylight.pathweaver.service.domain.exception;

import org.daylight.pathweaver.service.domain.common.ErrorMessages;

public class EntityNotFoundException extends PersistenceServiceException {
    private String message;

    public EntityNotFoundException(final String message) {
        this.message = message;
    }

    public EntityNotFoundException(String message, Throwable th) {
        super(message, th);
    }

    public EntityNotFoundException(Throwable th) {
        super(th);
    }

    public EntityNotFoundException(ErrorMessages messages) {
        this.message = messages.toString();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
