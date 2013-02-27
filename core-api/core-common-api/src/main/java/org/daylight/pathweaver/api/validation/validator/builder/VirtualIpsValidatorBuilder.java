package org.daylight.pathweaver.api.validation.validator.builder;

import org.daylight.pathweaver.api.validation.ValidatorBuilder;
import org.daylight.pathweaver.api.validation.validator.VirtualIpValidator;
import org.daylight.pathweaver.api.validation.verifier.*;

import org.daylight.pathweaver.core.api.v1.VirtualIps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.daylight.pathweaver.api.validation.context.HttpRequestType.POST;


@Component
@Scope("request")
public class VirtualIpsValidatorBuilder extends ValidatorBuilder<VirtualIps> {
    protected final int MAX_VIPS = 1;

    @Autowired
    public VirtualIpsValidatorBuilder(VirtualIpValidatorBuilder virtualIpValidatorBuilder) {
        super(VirtualIps.class);

        // POST EXPECTATIONS
        result(validationTarget().getVirtualIps()).must().haveSizeOfAtMost(MAX_VIPS).forContext(POST).withMessage(String.format("Must have at most %d virtual ip for the load balancer", MAX_VIPS));
        result(validationTarget().getVirtualIps()).if_().exist().then().must().adhereTo(new SharedOrNewVipVerifier()).forContext(POST).withMessage("Must specify either a shared or new virtual ip.");
        result(validationTarget().getVirtualIps()).if_().exist().then().must().delegateTo(new VirtualIpValidator(virtualIpValidatorBuilder).getValidator(), POST).forContext(POST);
    }
}
