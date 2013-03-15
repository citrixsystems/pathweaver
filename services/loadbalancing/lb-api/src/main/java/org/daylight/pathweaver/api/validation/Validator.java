package org.daylight.pathweaver.api.validation;

import org.daylight.pathweaver.api.validation.exception.ValidationChainExecutionException;
import org.daylight.pathweaver.api.validation.result.ValidatorResult;

public interface Validator<T> {
    ValidatorResult validate(T objectToValidate, Object context) throws ValidationChainExecutionException;
}
