package org.daylight.pathweaver.api.validation.validator.builder;

import org.daylight.pathweaver.api.validation.ValidatorBuilder;
import org.daylight.pathweaver.api.validation.validator.ConnectHealthMonitorValidator;
import org.daylight.pathweaver.api.validation.validator.HttpHealthMonitorValidator;
import org.daylight.pathweaver.api.validation.verifier.HealthMonitorTypeVerifier;
import org.daylight.pathweaver.core.api.v1.HealthMonitor;
import org.daylight.pathweaver.datamodel.CoreHealthMonitorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.daylight.pathweaver.api.validation.context.HttpRequestType.PUT;

@Component
@Scope("request")
public class HealthMonitorValidatorBuilder extends ValidatorBuilder<HealthMonitor> {

    @Autowired
    public HealthMonitorValidatorBuilder(ConnectMonitorValidatorBuilder connectMonitorValidatorBuilder, HttpMonitorValidatorBuilder httpMonitorValidatorBuilder) {
        super(HealthMonitor.class);

        // SHARED EXPECTATIONS
        must().exist().withMessage("Must provide a health monitor.");
        result(validationTarget().getType()).must().exist().withMessage("Must provide a type for the health monitor.");

        // PUT EXPECTATIONS
        if_().adhereTo(new HealthMonitorTypeVerifier(new CoreHealthMonitorType(CoreHealthMonitorType.CONNECT))).then().must().delegateTo(new ConnectHealthMonitorValidator(connectMonitorValidatorBuilder).getValidator(), PUT).forContext(PUT);
        if_().adhereTo(new HealthMonitorTypeVerifier(new CoreHealthMonitorType(CoreHealthMonitorType.HTTP))).then().must().delegateTo(new HttpHealthMonitorValidator(httpMonitorValidatorBuilder).getValidator(), PUT).forContext(PUT);
        if_().adhereTo(new HealthMonitorTypeVerifier(new CoreHealthMonitorType(CoreHealthMonitorType.HTTPS))).then().must().delegateTo(new HttpHealthMonitorValidator(httpMonitorValidatorBuilder).getValidator(), PUT).forContext(PUT);

    }
}
