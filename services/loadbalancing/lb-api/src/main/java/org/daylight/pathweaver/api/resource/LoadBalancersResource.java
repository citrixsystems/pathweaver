package org.daylight.pathweaver.api.resource;

import org.apache.log4j.Logger;
import org.daylight.pathweaver.api.resource.provider.CommonDependencyProvider;
import org.daylight.pathweaver.api.response.ResponseFactory;
import org.daylight.pathweaver.api.validation.context.HttpRequestType;
import org.daylight.pathweaver.api.validation.result.ValidatorResult;
import org.daylight.pathweaver.api.validation.validator.LoadBalancerValidator;
import org.daylight.pathweaver.core.api.v1.LoadBalancer;
import org.daylight.pathweaver.core.api.v1.LoadBalancers;
import org.daylight.pathweaver.service.domain.operation.CoreOperation;
import org.daylight.pathweaver.service.domain.pojo.MessageDataContainer;
import org.daylight.pathweaver.service.domain.repository.LoadBalancerRepository;
import org.daylight.pathweaver.service.domain.service.LoadBalancerService;
import org.daylight.pathweaver.service.domain.service.VirtualIpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.MediaType.*;

@Controller
@Scope("request")
public class LoadBalancersResource extends CommonDependencyProvider {
    private final Logger logger = Logger.getLogger(LoadBalancersResource.class);
    private HttpHeaders requestHeaders;
    private Integer accountId;

    @Autowired
    private LoadBalancerValidator validator;
    @Autowired
    private LoadBalancerService loadbalancerService;

    @Autowired
    private LoadBalancerRepository loadBalancerRepository;
    @Autowired
    private LoadBalancerResource loadBalancerResource;

    @POST
    @Consumes({APPLICATION_XML, APPLICATION_JSON})
    public Response create(LoadBalancer _loadBalancer) {
        logger.debug("Started processing a createLoadBalancer request");
        ValidatorResult result = validator.validate(_loadBalancer, HttpRequestType.POST);

        if (!result.passedValidation()) {
            logger.debug("validation of input request failed...");
            return ResponseFactory.getValidationFaultResponse(result);
        }

        logger.debug("validation of input request succeeded");

        try {
            org.daylight.pathweaver.service.domain.entity.LoadBalancer loadBalancer = getDozerMapper().map(_loadBalancer, org.daylight.pathweaver.service.domain.entity.LoadBalancer.class);
            loadBalancer.setAccountId(accountId);

            loadBalancer = loadbalancerService.create(loadBalancer);
            logger.debug("Successfully created loadBalancer in DB");
            MessageDataContainer dataContainer = new MessageDataContainer();
            dataContainer.setLoadBalancer(loadBalancer);

            getAsyncService().callAsyncLoadBalancingOperation(CoreOperation.CREATE_LOADBALANCER, dataContainer);
            return Response.status(Response.Status.ACCEPTED).entity(getDozerMapper().map(loadBalancer, LoadBalancer.class)).build();
        } catch (Exception e) {
            logger.debug("Caught an exception: " + e.toString());
            return ResponseFactory.getErrorResponse(e);
        }
    }

    @GET
    @Produces({APPLICATION_XML, APPLICATION_JSON, APPLICATION_ATOM_XML})
    public Response list() {
        LoadBalancers _loadbalancers = new LoadBalancers();
        List<org.daylight.pathweaver.service.domain.entity.LoadBalancer> loadbalancers = loadBalancerRepository.getByAccountId(accountId);
        for (org.daylight.pathweaver.service.domain.entity.LoadBalancer loadBalancer : loadbalancers) {
            _loadbalancers.getLoadBalancers().add(getDozerMapper().map(loadBalancer, org.daylight.pathweaver.core.api.v1.LoadBalancer.class, "SIMPLE_LB"));
        }
        return Response.status(Response.Status.OK).entity(_loadbalancers).build();

    }

    @Path("{id: [-+]?[0-9][0-9]*}")
    public LoadBalancerResource retrieveLoadBalancerResource(@PathParam("id") int id) {
        loadBalancerResource.setId(id);
        loadBalancerResource.setAccountId(accountId);
        return loadBalancerResource;
    }

    public void setRequestHeaders(HttpHeaders requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
}
