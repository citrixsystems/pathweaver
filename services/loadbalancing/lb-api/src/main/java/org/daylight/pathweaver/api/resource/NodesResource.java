package org.daylight.pathweaver.api.resource;

import org.apache.log4j.Logger;
import org.daylight.pathweaver.api.resource.provider.CommonDependencyProvider;
import org.daylight.pathweaver.api.response.ResponseFactory;
import org.daylight.pathweaver.api.validation.context.HttpRequestType;
import org.daylight.pathweaver.api.validation.result.ValidatorResult;
import org.daylight.pathweaver.api.validation.validator.NodesValidator;
import org.daylight.pathweaver.core.api.v1.Nodes;
import org.daylight.pathweaver.datamodel.CoreLoadBalancerStatus;
import org.daylight.pathweaver.service.domain.entity.LoadBalancer;
import org.daylight.pathweaver.service.domain.entity.Node;
import org.daylight.pathweaver.service.domain.exception.ImmutableEntityException;
import org.daylight.pathweaver.service.domain.operation.CoreOperation;
import org.daylight.pathweaver.service.domain.pojo.MessageDataContainer;
import org.daylight.pathweaver.service.domain.repository.LoadBalancerRepository;
import org.daylight.pathweaver.service.domain.repository.NodeRepository;
import org.daylight.pathweaver.service.domain.service.LoadBalancerService;
import org.daylight.pathweaver.service.domain.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import static org.daylight.pathweaver.datamodel.CoreLoadBalancerStatus.*;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static javax.ws.rs.core.MediaType.*;

@Controller
@Scope("request")
public class NodesResource extends CommonDependencyProvider {
    private final Logger logger = Logger.getLogger(NodesResource.class);
    private Integer accountId;
    private Integer loadBalancerId;
    private HttpHeaders requestHeaders;


    @Autowired
    private NodesValidator validator;
    @Autowired
    private NodeResource nodeResource;
    @Autowired
    private NodeService nodeService;
    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private LoadBalancerRepository loadBalancerRepository;
    @Autowired
    private LoadBalancerService loadBalancerService;

    @GET
    @Produces({APPLICATION_XML, APPLICATION_JSON, APPLICATION_ATOM_XML})
    public Response retrieveNodes() {
        try {
            Nodes returnNodes = new Nodes();
            for (Node node : nodeRepository.getNodesByAccountIdLoadBalancerId(loadBalancerId, accountId)) {
                returnNodes.getNodes().add(getDozerMapper().map(node, org.daylight.pathweaver.core.api.v1.Node.class));
            }
            return Response.status(Response.Status.OK).entity(returnNodes).build();
        } catch (Exception e) {
            logger.debug("Caught an exception: " + e.toString());
            return ResponseFactory.getErrorResponse(e);
        }
    }

    @POST
    @Consumes({APPLICATION_XML, APPLICATION_JSON})
    public Response createNodes(Nodes _nodes) {
        ValidatorResult result = validator.validate(_nodes, HttpRequestType.POST);

        if (!result.passedValidation()) {
            return ResponseFactory.getValidationFaultResponse(result);
        }

        try {
            loadBalancerRepository.getByIdAndAccountId(loadBalancerId, accountId);

            org.daylight.pathweaver.core.api.v1.LoadBalancer apiLb = new org.daylight.pathweaver.core.api.v1.LoadBalancer();
            Nodes nodes = new Nodes();
            nodes.getNodes().addAll(_nodes.getNodes());
            apiLb.setNodes(nodes);
            LoadBalancer domainLb = getDozerMapper().map(apiLb, LoadBalancer.class);
            domainLb.setId(loadBalancerId);
            domainLb.setAccountId(accountId);

            domainLb.setUserName(getUserName(requestHeaders));

            Nodes returnNodes = new Nodes();
            Set<Node> dbnodes = nodeService.createNodes(domainLb);
            for (Node node : dbnodes) {
                returnNodes.getNodes().add(getDozerMapper().map(node, org.daylight.pathweaver.core.api.v1.Node.class));
            }

            MessageDataContainer dataContainer = new MessageDataContainer();
            dataContainer.setLoadBalancer(domainLb);

            getAsyncService().callAsyncLoadBalancingOperation(CoreOperation.CREATE_NODES, dataContainer);
            return Response.status(Response.Status.ACCEPTED).entity(returnNodes).build();
        } catch (Exception e) {
            logger.debug("Caught an exception: " + e.toString());
            return ResponseFactory.getErrorResponse(e);
        }
    }

    @DELETE
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    public Response deleteNodes(@QueryParam("id") List<Integer> ids) {
        try {

            MessageDataContainer msg = new MessageDataContainer();

            boolean isLbEditable;
            LoadBalancer dlb = new LoadBalancer();
            List<String> validationErrors;
            dlb.setId(loadBalancerId);
            Collections.sort(ids);
            isLbEditable = loadBalancerRepository.testAndSetStatus(accountId, loadBalancerId, PENDING_DELETE, false);
            if (!isLbEditable) {
                throw new ImmutableEntityException("LoadBalancer is not ACTIVE");
            }

            validationErrors = nodeService.prepareForNodesDeletion(accountId, loadBalancerId, ids);
            if (validationErrors.size() > 0) {
                LoadBalancer lb = loadBalancerRepository.getByIdAndAccountId(loadBalancerId, accountId);
                lb.setStatus(CoreLoadBalancerStatus.ACTIVE);
                loadBalancerRepository.update(lb);
                return getValidationFaultResponse(validationErrors);
            }

            msg.setIds(ids);
            msg.setAccountId(accountId);
            msg.setLoadBalancerId(loadBalancerId);
            msg.setUserName(getUserName(requestHeaders));
            getAsyncService().callAsyncLoadBalancingOperation(CoreOperation.DELETE_NODES, msg);
            return Response.status(Response.Status.ACCEPTED).build();
        } catch (Exception ex) {
            logger.debug("Caught an exception: " + ex.toString());
            return ResponseFactory.getErrorResponse(ex, null, null);
        }
    }


    @Path("{id: [-+]?[1-9][0-9]*}")
    public NodeResource retrieveNodeResource(@PathParam("id") int id) {
        nodeResource.setId(id);
        nodeResource.setAccountId(accountId);
        nodeResource.setLbId(loadBalancerId);
        return nodeResource;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public void setLoadBalancerId(Integer loadBalancerId) {
        this.loadBalancerId = loadBalancerId;
    }
}
