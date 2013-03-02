package org.daylight.pathweaver.api.validation.validator;

import org.daylight.pathweaver.api.validation.Validator;
import org.daylight.pathweaver.api.validation.result.ValidatorResult;
import org.daylight.pathweaver.api.validation.validator.builder.NodesValidatorBuilder;
import org.daylight.pathweaver.core.api.v1.Nodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.daylight.pathweaver.api.validation.ValidatorBuilder.build;

@Component
@Scope("request")
public class NodesValidator implements ResourceValidator<Nodes> {
    private Validator<Nodes> validator;
    private NodesValidatorBuilder ruleBuilder;

    @Autowired
    public NodesValidator(NodesValidatorBuilder ruleBuilder) {
        this.ruleBuilder = ruleBuilder;
        validator = build(ruleBuilder);
    }

    @Override
    public ValidatorResult validate(Nodes nodes, Object httpRequestType) {
        ValidatorResult result = validator.validate(nodes, httpRequestType);
        return ValidatorUtilities.removeEmptyMessages(result);
    }

    @Override
    public Validator<Nodes> getValidator() {
        return validator;
    }
}
