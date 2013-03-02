package org.daylight.pathweaver.api.validation.validator;

import org.daylight.pathweaver.api.validation.validator.builder.HealthMonitorValidatorBuilder;
import org.daylight.pathweaver.core.api.v1.HealthMonitor;
import org.daylight.pathweaver.api.validation.Validator;
import org.daylight.pathweaver.api.validation.ValidatorBuilder;
import org.daylight.pathweaver.api.validation.result.ValidatorResult;
import org.daylight.pathweaver.api.validation.verifier.HealthMonitorTypeVerifier;
import org.daylight.pathweaver.api.validation.verifier.MustBeIntegerInRange;
import org.daylight.pathweaver.datamodel.CoreHealthMonitorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.daylight.pathweaver.api.validation.ValidatorBuilder.build;
import static org.daylight.pathweaver.api.validation.context.HttpRequestType.PUT;

@Component
@Scope("request")
public class HealthMonitorValidator implements ResourceValidator<HealthMonitor> {
    private Validator<HealthMonitor> validator;
    private HealthMonitorValidatorBuilder ruleBuilder;

    @Autowired
    public HealthMonitorValidator(HealthMonitorValidatorBuilder ruleBuilder) {
        this.ruleBuilder = ruleBuilder;
        validator = build(ruleBuilder);
    }

    @Override
    public ValidatorResult validate(HealthMonitor healthMonitor, Object context) {
        return validator.validate(healthMonitor, context);
    }

    @Override
    public Validator<HealthMonitor> getValidator() {
        return validator;
    }
}
