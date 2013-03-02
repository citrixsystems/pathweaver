package org.daylight.pathweaver.api.resource;

import org.apache.log4j.Logger;
import org.daylight.pathweaver.api.resource.provider.CommonDependencyProvider;
import org.daylight.pathweaver.api.response.ResponseFactory;
import org.daylight.pathweaver.api.validation.context.HttpRequestType;
import org.daylight.pathweaver.api.validation.result.ValidatorResult;
import org.daylight.pathweaver.api.validation.validator.ConnectionThrottleValidator;
import org.daylight.pathweaver.core.api.v1.ConnectionThrottle;
import org.daylight.pathweaver.service.domain.entity.LoadBalancer;
import org.daylight.pathweaver.service.domain.operation.CoreOperation;
import org.daylight.pathweaver.service.domain.pojo.MessageDataContainer;
import org.daylight.pathweaver.service.domain.repository.ConnectionThrottleRepository;
import org.daylight.pathweaver.service.domain.service.ConnectionThrottleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.*;

@Controller
@Scope("request")
public class ConnectionThrottleResource extends CommonDependencyProvider {
    private final Logger logger = Logger.getLogger(ConnectionThrottleResource.class);
    private Integer accountId;
    private Integer loadBalancerId;

    @Autowired
    private ConnectionThrottleValidator validator;
    @Autowired
    private ConnectionThrottleService connectionThrottleService;
    @Autowired
    private ConnectionThrottleRepository repository;

    @GET
    @Produces({APPLICATION_XML, APPLICATION_JSON, APPLICATION_ATOM_XML})
    public Response retrieveConnectionThrottle() {
        try {
            ConnectionThrottle connectionThrottle = getDozerMapper().map(repository.getByLoadBalancerId(loadBalancerId), ConnectionThrottle.class);
            return Response.status(Response.Status.OK).entity(connectionThrottle).build();
        } catch (Exception e) {
            return ResponseFactory.getErrorResponse(e);
        }
    }

    @PUT
    @Consumes({APPLICATION_XML, APPLICATION_JSON})
    public Response updateConnectionThrottle(ConnectionThrottle _connectionThrottle) {
        ValidatorResult result = validator.validate(_connectionThrottle, HttpRequestType.PUT);

        if (!result.passedValidation()) {
            return ResponseFactory.getValidationFaultResponse(result);
        }

        try {

            org.daylight.pathweaver.service.domain.entity.ConnectionThrottle connectionThrottle = getDozerMapper().map(_connectionThrottle, org.daylight.pathweaver.service.domain.entity.ConnectionThrottle.class);
            connectionThrottle = connectionThrottleService.update(loadBalancerId, connectionThrottle);

            MessageDataContainer data = new MessageDataContainer();
            LoadBalancer loadBalancer = new LoadBalancer();
            loadBalancer.setAccountId(accountId);
            loadBalancer.setId(loadBalancerId);
            loadBalancer.setConnectionThrottle(connectionThrottle);
            data.setLoadBalancer(loadBalancer);

            getAsyncService().callAsyncLoadBalancingOperation(CoreOperation.UPDATE_CONNECTION_THROTTLE, data);
            _connectionThrottle = getDozerMapper().map(connectionThrottle, ConnectionThrottle.class);
            return Response.status(Response.Status.ACCEPTED).entity(_connectionThrottle).build();
        } catch (Exception e) {
            return ResponseFactory.getErrorResponse(e);
        }
    }

    @DELETE
    public Response deleteConnectionThrottle() {
        try {
            MessageDataContainer data = new MessageDataContainer();
            LoadBalancer loadBalancer = new LoadBalancer();
            loadBalancer.setAccountId(accountId);
            loadBalancer.setId(loadBalancerId);
            data.setLoadBalancer(loadBalancer);

            connectionThrottleService.preDelete(loadBalancerId);
            getAsyncService().callAsyncLoadBalancingOperation(CoreOperation.DELETE_CONNECTION_THROTTLE, data);
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
