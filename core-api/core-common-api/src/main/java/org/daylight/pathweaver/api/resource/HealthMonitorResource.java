package org.daylight.pathweaver.api.resource;

import org.apache.log4j.Logger;
import org.daylight.pathweaver.api.resource.provider.CommonDependencyProvider;
import org.daylight.pathweaver.api.response.ResponseFactory;
import org.daylight.pathweaver.api.validation.context.HttpRequestType;
import org.daylight.pathweaver.api.validation.result.ValidatorResult;
import org.daylight.pathweaver.api.validation.validator.HealthMonitorValidator;
import org.daylight.pathweaver.core.api.v1.HealthMonitor;
import org.daylight.pathweaver.service.domain.entity.LoadBalancer;
import org.daylight.pathweaver.service.domain.operation.CoreOperation;
import org.daylight.pathweaver.service.domain.pojo.MessageDataContainer;
import org.daylight.pathweaver.service.domain.repository.HealthMonitorRepository;
import org.daylight.pathweaver.service.domain.service.HealthMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.*;

@Controller
@Scope("request")
public class HealthMonitorResource extends CommonDependencyProvider {
    private final Logger LOG = Logger.getLogger(HealthMonitorResource.class);
    protected Integer accountId;
    protected Integer loadBalancerId;

    @Autowired
    protected HealthMonitorValidator validator;
    @Autowired
    protected HealthMonitorService service;
    @Autowired
    protected HealthMonitorRepository repository;

    @GET
    @Produces({APPLICATION_XML, APPLICATION_JSON, APPLICATION_ATOM_XML})
    public Response retrieveHealthMonitor() {
        try {
            HealthMonitor healthMonitor = dozerMapper.map(repository.getByLoadBalancerId(loadBalancerId), HealthMonitor.class);
            return Response.status(Response.Status.OK).entity(healthMonitor).build();
        } catch (Exception e) {
            return ResponseFactory.getErrorResponse(e);
        }
    }

    @PUT
    @Consumes({APPLICATION_XML, APPLICATION_JSON})
    public Response updateHealthMonitor(HealthMonitor _healthMonitor) {
        ValidatorResult result = validator.validate(_healthMonitor, HttpRequestType.PUT);

        if (!result.passedValidation()) {
            return ResponseFactory.getValidationFaultResponse(result);
        }

        try {
            MessageDataContainer data = new MessageDataContainer();
            LoadBalancer loadBalancer = new LoadBalancer();
            loadBalancer.setAccountId(accountId);
            loadBalancer.setId(loadBalancerId);
            data.setLoadBalancer(loadBalancer);

            org.daylight.pathweaver.service.domain.entity.HealthMonitor healthMonitor = dozerMapper.map(_healthMonitor, org.daylight.pathweaver.service.domain.entity.HealthMonitor.class);
            healthMonitor = service.update(loadBalancerId, healthMonitor);
            asyncService.callAsyncLoadBalancingOperation(CoreOperation.UPDATE_HEALTH_MONITOR, data);
            _healthMonitor = dozerMapper.map(healthMonitor, HealthMonitor.class);
            return Response.status(Response.Status.ACCEPTED).entity(_healthMonitor).build();
        } catch (Exception e) {
            return ResponseFactory.getErrorResponse(e);
        }
    }

    @DELETE
    public Response deleteHealthMonitor() {
        try {
            MessageDataContainer data = new MessageDataContainer();
            LoadBalancer loadBalancer = new LoadBalancer();
            loadBalancer.setAccountId(accountId);
            loadBalancer.setId(loadBalancerId);
            data.setLoadBalancer(loadBalancer);

            service.preDelete(loadBalancerId);
            asyncService.callAsyncLoadBalancingOperation(CoreOperation.DELETE_HEALTH_MONITOR, data);
            return Response.status(Response.Status.ACCEPTED).build();
        } catch (Exception e) {
            return ResponseFactory.getErrorResponse(e);
        }
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public void setLoadBalancerId(Integer loadBalancerId) {
        this.loadBalancerId = loadBalancerId;
    }
}
