package org.daylight.pathweaver.api.resource;

import org.apache.log4j.Logger;
import org.daylight.pathweaver.api.resource.provider.CommonDependencyProvider;
import org.daylight.pathweaver.api.response.ResponseFactory;
import org.daylight.pathweaver.api.validation.context.HttpRequestType;
import org.daylight.pathweaver.api.validation.result.ValidatorResult;
import org.daylight.pathweaver.api.validation.validator.NodeValidator;
import org.daylight.pathweaver.service.domain.entity.LoadBalancer;
import org.daylight.pathweaver.service.domain.entity.Node;
import org.daylight.pathweaver.service.domain.operation.CoreOperation;
import org.daylight.pathweaver.service.domain.pojo.MessageDataContainer;
import org.daylight.pathweaver.service.domain.repository.LoadBalancerRepository;
import org.daylight.pathweaver.service.domain.repository.NodeRepository;
import org.daylight.pathweaver.service.domain.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.MediaType.*;

@Controller
@Scope("request")
public class NodeResource extends CommonDependencyProvider {
    private final Logger logger = Logger.getLogger(NodeResource.class);
    private int id;
    private Integer accountId;
    private Integer loadBalancerId;
    private HttpHeaders requestHeaders;

    @Autowired
    private NodeValidator validator;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private LoadBalancerRepository loadBalancerRepository;

    @GET
    @Produces({APPLICATION_XML, APPLICATION_JSON, APPLICATION_ATOM_XML})
    public Response retrieveNode() {
        Node dnode;
        org.daylight.pathweaver.core.api.v1.Node rnode;
        try {
            dnode = nodeRepository.getNodesByLoadBalancer(loadBalancerRepository.getByIdAndAccountId(loadBalancerId, accountId), id);
            rnode = getDozerMapper().map(dnode, org.daylight.pathweaver.core.api.v1.Node.class);
            return Response.status(200).entity(rnode).build();
        } catch (Exception e) {
            return ResponseFactory.getErrorResponse(e);
        }
    }

    @PUT
    @Consumes({APPLICATION_XML, APPLICATION_JSON})
    public Response updateNode(org.daylight.pathweaver.core.api.v1.Node _node) {
        ValidatorResult result = validator.validate(_node, HttpRequestType.PUT);

        if (!result.passedValidation()) {
            return ResponseFactory.getValidationFaultResponse(result);
        }

        try {
            _node.setId(id);
            org.daylight.pathweaver.core.api.v1.LoadBalancer apiLb = new org.daylight.pathweaver.core.api.v1.LoadBalancer();

            org.daylight.pathweaver.core.api.v1.Nodes nodes = new org.daylight.pathweaver.core.api.v1.Nodes();
            nodes.getNodes().add(_node);
            apiLb.setNodes(nodes);
            LoadBalancer domainLb = getDozerMapper().map(apiLb, LoadBalancer.class);
            domainLb.setId(loadBalancerId);
            domainLb.setAccountId(accountId);
            if (requestHeaders != null) {
                domainLb.setUserName(requestHeaders.getRequestHeader("X-PP-User").get(0));
            }

            LoadBalancer dbLb = nodeService.updateNode(domainLb);

            MessageDataContainer dataContainer = new MessageDataContainer();
            dataContainer.setLoadBalancer(dbLb);

            getAsyncService().callAsyncLoadBalancingOperation(CoreOperation.UPDATE_NODE, dataContainer);
            return Response.status(Response.Status.ACCEPTED).build();
        } catch (Exception e) {
            return ResponseFactory.getErrorResponse(e);
        }
    }

    @DELETE
    public Response deleteNode() {
        try {
            MessageDataContainer dataContainer = new MessageDataContainer();
            dataContainer.setAccountId(accountId);
            dataContainer.setLoadBalancerId(loadBalancerId);

            List<Integer> ids = new ArrayList<Integer>();
            ids.add(id);
            dataContainer.setIds(ids);

            getAsyncService().callAsyncLoadBalancingOperation(CoreOperation.DELETE_NODES, dataContainer);
            return Response.status(Response.Status.ACCEPTED).build();
        } catch (Exception e) {
            return ResponseFactory.getErrorResponse(e);
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public void setLbId(Integer loadBalancerId) {
        this.loadBalancerId = loadBalancerId;
    }
}
