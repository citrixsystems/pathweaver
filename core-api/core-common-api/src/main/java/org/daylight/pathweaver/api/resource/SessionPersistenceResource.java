package org.daylight.pathweaver.api.resource;

import org.apache.log4j.Logger;
import org.daylight.pathweaver.api.resource.provider.CommonDependencyProvider;
import org.daylight.pathweaver.api.response.ResponseFactory;
import org.daylight.pathweaver.api.validation.context.HttpRequestType;
import org.daylight.pathweaver.api.validation.result.ValidatorResult;
import org.daylight.pathweaver.api.validation.validator.SessionPersistenceValidator;
import org.daylight.pathweaver.core.api.v1.SessionPersistence;
import org.daylight.pathweaver.service.domain.entity.LoadBalancer;
import org.daylight.pathweaver.service.domain.operation.CoreOperation;
import org.daylight.pathweaver.service.domain.pojo.MessageDataContainer;
import org.daylight.pathweaver.service.domain.repository.SessionPersistenceRepository;
import org.daylight.pathweaver.service.domain.service.SessionPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.*;

@Controller
@Scope("request")
public class SessionPersistenceResource extends CommonDependencyProvider {
    private final Logger LOG = Logger.getLogger(SessionPersistenceResource.class);
    private Integer accountId;
    private Integer loadBalancerId;

    @Autowired
    protected SessionPersistenceValidator validator;
    @Autowired
    protected SessionPersistenceService service;
    @Autowired
    protected SessionPersistenceRepository repository;

    @GET
    @Produces({APPLICATION_XML, APPLICATION_JSON, APPLICATION_ATOM_XML})
    public Response retrieveSessionPersistence() {
        try {
            SessionPersistence sessionPersistence = dozerMapper.map(repository.getByLoadBalancerId(loadBalancerId), SessionPersistence.class);
            return Response.status(Response.Status.OK).entity(sessionPersistence).build();
        } catch (Exception e) {
            return ResponseFactory.getErrorResponse(e);
        }
    }

    @PUT
    @Consumes({APPLICATION_XML, APPLICATION_JSON})
    public Response createOrUpdateSessionPersistence(SessionPersistence _sessionPersistence) {
        ValidatorResult result = validator.validate(_sessionPersistence, HttpRequestType.PUT);

        if (!result.passedValidation()) {
            return ResponseFactory.getValidationFaultResponse(result);
        }

        try {
            MessageDataContainer data = new MessageDataContainer();
            LoadBalancer loadBalancer = new LoadBalancer();
            loadBalancer.setAccountId(accountId);
            loadBalancer.setId(loadBalancerId);
            data.setLoadBalancer(loadBalancer);

            org.daylight.pathweaver.service.domain.entity.SessionPersistence sessionPersistence = dozerMapper.map(_sessionPersistence, org.daylight.pathweaver.service.domain.entity.SessionPersistence.class);
            sessionPersistence = service.update(loadBalancerId, sessionPersistence);
            asyncService.callAsyncLoadBalancingOperation(CoreOperation.UPDATE_SESSION_PERSISTENCE, data);
            _sessionPersistence = dozerMapper.map(sessionPersistence, SessionPersistence.class);
            return Response.status(Response.Status.ACCEPTED).entity(_sessionPersistence).build();
        } catch (Exception e) {
            return ResponseFactory.getErrorResponse(e);
        }
    }

    @DELETE
    public Response deleteSessionPersistence() {
        try {
            MessageDataContainer data = new MessageDataContainer();
            LoadBalancer loadBalancer = new LoadBalancer();
            loadBalancer.setAccountId(accountId);
            loadBalancer.setId(loadBalancerId);
            data.setLoadBalancer(loadBalancer);
            
            service.preDelete(loadBalancerId);
            asyncService.callAsyncLoadBalancingOperation(CoreOperation.DELETE_SESSION_PERSISTENCE, data);
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
