package org.daylight.pathweaver.api.validation.validator.builder;

import org.daylight.pathweaver.api.validation.ValidatorBuilder;
import org.daylight.pathweaver.api.validation.validator.NodeValidator;
import org.daylight.pathweaver.api.validation.verifier.ActiveNodeVerifier;
import org.daylight.pathweaver.api.validation.verifier.DuplicateNodeVerifier;
import org.daylight.pathweaver.core.api.v1.Nodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.daylight.pathweaver.api.validation.context.HttpRequestType.POST;

@Component
@Scope("request")
public class NodesValidatorBuilder extends ValidatorBuilder<Nodes> {
    private final int MIN_NODES = 1;
    private final int MAX_NODES = 25;

    @Autowired
    public NodesValidatorBuilder(NodeValidatorBuilder nodeValidatorBuilder) {
        super(Nodes.class);

        // POST EXPECTATIONS
        result(validationTarget().getNodes()).must().exist().forContext(POST).withMessage("Must provide at least 1 node for the load balancer.");
        result(validationTarget().getNodes()).must().adhereTo(new DuplicateNodeVerifier()).forContext(POST).withMessage("Duplicate nodes detected. Please ensure that the ip address and port are unique for each node.");
        result(validationTarget().getNodes()).must().adhereTo(new ActiveNodeVerifier()).forContext(POST).withMessage("Please ensure that at least 1 node has an ENABLED condition.");
        result(validationTarget().getNodes()).must().haveSizeOfAtLeast(MIN_NODES).forContext(POST).withMessage(String.format("Must have at least %d node(s).", MIN_NODES));
        result(validationTarget().getNodes()).must().haveSizeOfAtMost(MAX_NODES).forContext(POST).withMessage(String.format("Must not provide more than %d nodes per load balancer.", MAX_NODES));
        result(validationTarget().getNodes()).if_().exist().then().must().delegateTo(new NodeValidator(nodeValidatorBuilder).getValidator(), POST).forContext(POST);

    }
}
