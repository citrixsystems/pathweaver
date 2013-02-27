package org.daylight.pathweaver.api.validation.validator;

import org.daylight.pathweaver.api.validation.Validator;
import org.daylight.pathweaver.api.validation.validator.builder.LoadBalancerValidatorBuilder;
import org.daylight.pathweaver.api.validation.result.ValidatorResult;
import org.daylight.pathweaver.api.validation.validator.builder.LoadBalancerValidatorBuilder;
import org.daylight.pathweaver.core.api.v1.LoadBalancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.daylight.pathweaver.api.validation.ValidatorBuilder.build;

@Component
@Scope("request")
public class LoadBalancerValidator implements ResourceValidator<LoadBalancer> {
    protected Validator<LoadBalancer> validator;
    protected LoadBalancerValidatorBuilder ruleBuilder;

    @Autowired
    public LoadBalancerValidator(LoadBalancerValidatorBuilder ruleBuilder) {
        this.ruleBuilder = ruleBuilder;
        validator = build(ruleBuilder);
    }

    @Override
    public ValidatorResult validate(LoadBalancer lb, Object httpRequestType) {
        ValidatorResult result = validator.validate(lb, httpRequestType);
        return ValidatorUtilities.removeEmptyMessages(result);
    }

    @Override
    public Validator<LoadBalancer> getValidator() {
        return validator;
    }

}
