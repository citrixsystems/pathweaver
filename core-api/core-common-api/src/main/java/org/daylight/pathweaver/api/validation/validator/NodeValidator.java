package org.daylight.pathweaver.api.validation.validator;

import org.daylight.pathweaver.api.validation.Validator;
import org.daylight.pathweaver.api.validation.validator.builder.NodeValidatorBuilder;
import org.daylight.pathweaver.api.validation.result.ValidatorResult;
import org.daylight.pathweaver.api.validation.validator.builder.NodeValidatorBuilder;
import org.daylight.pathweaver.core.api.v1.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.daylight.pathweaver.api.validation.ValidatorBuilder.build;

@Component
@Scope("request")
public class NodeValidator implements ResourceValidator<Node> {
    private Validator<Node> validator;
    private NodeValidatorBuilder ruleBuilder;

    @Autowired
    public NodeValidator(NodeValidatorBuilder ruleBuilder) {
        this.ruleBuilder = ruleBuilder;
        validator = build(ruleBuilder);
    }

    @Override
    public ValidatorResult validate(Node node, Object type) {
        ValidatorResult result = validator.validate(node, type);
        return ValidatorUtilities.removeEmptyMessages(result);
    }

    @Override
    public Validator<Node> getValidator() {
        return validator;
    }
}
