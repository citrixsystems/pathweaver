package org.daylight.pathweaver.api.validation.validator;

import org.daylight.pathweaver.api.validation.Validator;
import org.daylight.pathweaver.api.validation.result.ValidatorResult;
import org.daylight.pathweaver.api.validation.validator.builder.HttpMonitorValidatorBuilder;
import org.daylight.pathweaver.core.api.v1.HealthMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.daylight.pathweaver.api.validation.ValidatorBuilder.build;

@Component
@Scope("request")
public class HttpHealthMonitorValidator implements ResourceValidator<HealthMonitor> {
    private Validator<HealthMonitor> validator;
    private HttpMonitorValidatorBuilder ruleBuilder;

    @Autowired
    public HttpHealthMonitorValidator(HttpMonitorValidatorBuilder ruleBuilder) {
        this.ruleBuilder = ruleBuilder;
        validator = build(ruleBuilder);
    }

    @Override
    public ValidatorResult validate(HealthMonitor monitor, Object context) {
        ValidatorResult result = validator.validate(monitor, context);
        return ValidatorUtilities.removeEmptyMessages(result);
    }

    @Override
    public Validator<HealthMonitor> getValidator() {
        return validator;
    }
}
