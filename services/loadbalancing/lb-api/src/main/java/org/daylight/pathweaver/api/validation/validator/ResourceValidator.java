package org.daylight.pathweaver.api.validation.validator;

import org.daylight.pathweaver.api.validation.Validator;
import org.daylight.pathweaver.api.validation.result.ValidatorResult;

public interface ResourceValidator<T> {
    public ValidatorResult validate(T objectToValidate, Object context);

    public Validator<T> getValidator();
}
