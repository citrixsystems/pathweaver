package org.daylight.pathweaver.api.async;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.daylight.pathweaver.api.atom.EntryHelper;
import org.daylight.pathweaver.api.helper.NodesHelper;

import org.daylight.pathweaver.service.domain.entity.LoadBalancer;
import org.daylight.pathweaver.service.domain.entity.Node;
import org.daylight.pathweaver.service.domain.exception.EntityNotFoundException;
import org.daylight.pathweaver.service.domain.pojo.MessageDataContainer;
import org.daylight.pathweaver.service.domain.repository.LoadBalancerRepository;
import org.daylight.pathweaver.service.domain.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;

import static org.daylight.pathweaver.api.atom.EntryHelper.CREATE_NODE_TITLE;
import static org.daylight.pathweaver.datamodel.CoreLoadBalancerStatus.*;
import static org.daylight.pathweaver.datamodel.CoreNodeStatus.*;
import static org.daylight.pathweaver.service.domain.service.helpers.AlertType.*;
import static org.daylight.pathweaver.service.domain.event.entity.EventType.*;
import static org.daylight.pathweaver.service.domain.event.entity.CategoryType.*;
import static org.daylight.pathweaver.service.domain.event.entity.EventSeverity.*;

@Component
public class CreateNodesListener extends BaseListener {
    private final Log logger = LogFactory.getLog(CreateNodesListener.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LoadBalancerRepository loadBalancerRepository;

    @Override
    public void doOnMessage(final Message message) throws Exception {
        logger.debug("Entering " + getClass());
        logger.debug(message);

        LoadBalancer dbLoadBalancer;

        MessageDataContainer dataContainer = getDataContainerFromMessage(message);
        LoadBalancer queueLb = dataContainer.getLoadBalancer();

        try {
            dbLoadBalancer = loadBalancerRepository.getByIdAndAccountId(queueLb.getId(), queueLb.getAccountId());
        } catch (EntityNotFoundException enfe) {
            String alertDescription = String.format("Load balancer '%d' not found in database.", queueLb.getId());
            logger.error(alertDescription, enfe);
            notificationService.saveAlert(queueLb.getAccountId(), queueLb.getId(), enfe, DATABASE_FAILURE.name(), alertDescription);
            sendErrorToEventResource(queueLb);
            return;
        }

        try {
            logger.debug("Setting nodes in LBDevice...");
            getReverseProxyLoadBalancerService().createNodes(dbLoadBalancer.getAccountId(), dbLoadBalancer.getId(), dbLoadBalancer.getNodes());
            logger.debug("Nodes successfully set.");
        } catch (Exception e) {
            dbLoadBalancer.setStatus(ERROR);
            NodesHelper.setNodesToStatus(dbLoadBalancer, OFFLINE);
            loadBalancerRepository.update(dbLoadBalancer);
            String alertDescription = "Error setting nodes in LB Device for loadbalancer #" + dbLoadBalancer.getId();
            logger.error(alertDescription, e);
            notificationService.saveAlert(dbLoadBalancer.getAccountId(), dbLoadBalancer.getId(), e, LBDEVICE_FAILURE.name(), alertDescription);
            sendErrorToEventResource(queueLb);
            return;
        }

        // Update load balancer in DB
        dbLoadBalancer.setStatus(ACTIVE);
        NodesHelper.setNodesToStatus(queueLb, dbLoadBalancer, ONLINE);
        loadBalancerRepository.update(dbLoadBalancer);

        // Add atom entries for new nodes only
        for (Node dbNode : dbLoadBalancer.getNodes()) {
            for (Node queueNode : queueLb.getNodes()) {
                if (queueNode.getAddress().equals(dbNode.getAddress()) && queueNode.getPort().equals(dbNode.getPort()))  {
                    notificationService.saveNodeEvent(queueLb.getUserName(), dbLoadBalancer.getAccountId(), dbLoadBalancer.getId(),
                            dbNode.getId(), CREATE_NODE_TITLE, EntryHelper.createNodeSummary(dbNode), CREATE_NODE, CREATE, INFO);
                }
            }
        }

        logger.info(String.format("Create nodes operation successfully completed for load balancer '%d'", dbLoadBalancer.getId()));
    }

    private void sendErrorToEventResource(LoadBalancer lb) {
        String title = "Error Creating Node";
        String desc = "Could not create the node at this time";
        for (Node node : lb.getNodes()) {
            notificationService.saveNodeEvent(lb.getUserName(), lb.getAccountId(), lb.getId(), node.getId(), title, desc, CREATE_NODE, CREATE, CRITICAL);
        }
    }
}
