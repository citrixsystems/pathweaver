package org.daylight.pathweaver.api.validation.validator;

import org.daylight.pathweaver.api.validation.Validator;
import org.daylight.pathweaver.api.validation.result.ValidatorResult;
import org.daylight.pathweaver.api.validation.validator.builder.VirtualIpsValidatorBuilder;
import org.daylight.pathweaver.core.api.v1.VirtualIps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.daylight.pathweaver.api.validation.ValidatorBuilder.build;

@Component
@Scope("request")
public class VirtualIpsValidator implements ResourceValidator<VirtualIps> {
    private Validator<VirtualIps> validator;
    private VirtualIpsValidatorBuilder ruleBuilder;

    @Autowired
    public VirtualIpsValidator(VirtualIpsValidatorBuilder ruleBuilder) {
        this.ruleBuilder = ruleBuilder;
        validator = build(ruleBuilder);
    }

    @Override
    public ValidatorResult validate(VirtualIps virtualIps, Object context) {
        ValidatorResult result = validator.validate(virtualIps, context);
        return ValidatorUtilities.removeEmptyMessages(result);
    }

    @Override
    public Validator<VirtualIps> getValidator() {
        return validator;
    }
}
