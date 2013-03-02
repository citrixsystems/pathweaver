package org.daylight.pathweaver.api.async;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.daylight.pathweaver.common.converters.StringConverter;
import org.daylight.pathweaver.datamodel.CoreLoadBalancerStatus;
import org.daylight.pathweaver.service.domain.entity.LoadBalancer;
import org.daylight.pathweaver.service.domain.entity.Node;
import org.daylight.pathweaver.service.domain.exception.EntityNotFoundException;
import org.daylight.pathweaver.service.domain.pojo.MessageDataContainer;
import org.daylight.pathweaver.service.domain.repository.LoadBalancerRepository;
import org.daylight.pathweaver.service.domain.repository.NodeRepository;
import org.daylight.pathweaver.service.domain.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.daylight.pathweaver.service.domain.common.AlertType.DATABASE_FAILURE;
import static org.daylight.pathweaver.service.domain.common.AlertType.LBDEVICE_FAILURE;
import static org.daylight.pathweaver.service.domain.event.entity.CategoryType.DELETE;
import static org.daylight.pathweaver.service.domain.event.entity.EventSeverity.CRITICAL;
import static org.daylight.pathweaver.service.domain.event.entity.EventSeverity.INFO;
import static org.daylight.pathweaver.service.domain.event.entity.EventType.DELETE_NODE;


@Component
public class DeleteNodeListener extends BaseListener {

    private final Log logger = LogFactory.getLog(DeleteNodeListener.class);

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private LoadBalancerRepository loadBalancerRepository;
    @Autowired
    private NodeRepository nodeRepository;

    @Override
    public void doOnMessage(final Message message) throws Exception {
        logger.debug("Entering " + getClass());
        logger.debug(message);
        MessageDataContainer msg = getDataContainerFromMessage(message);
        LoadBalancer dbLoadBalancer;
        try {
            dbLoadBalancer = loadBalancerRepository.getByIdAndAccountId(msg.getLoadBalancerId(), msg.getAccountId());
        } catch (EntityNotFoundException enfe) {
            String alertDescription = String.format("Load balancer '%d' not found in database.", msg.getLoadBalancerId());
            logger.error(alertDescription, enfe);
            notificationService.saveAlert(msg.getAccountId(), msg.getLoadBalancerId(), enfe, DATABASE_FAILURE.name(), alertDescription);
            sendErrorToEventResource(msg);
            return;
        }

        String nodesToDeleteIdString = StringConverter.integersAsString(msg.getIds());
        Set<Node> nodesToDelete = new HashSet<Node>();
        for (Node node : dbLoadBalancer.getNodes()) {
            if (msg.getIds().contains(node.getId())) {
                nodesToDelete.add(node);
            }
        }

        try {
            logger.debug(String.format("Removing nodes '[%s]' from load balancer '%d' in Adapter...", nodesToDeleteIdString, msg.getLoadBalancerId()));
            getReverseProxyLoadBalancerService().deleteNodes(msg.getAccountId(), msg.getLoadBalancerId(), nodesToDelete);
            logger.debug(String.format("Successfully removed nodes '[%s]' from load balancer '%d' in Adapter.", nodesToDeleteIdString, msg.getLoadBalancerId()));
        } catch (Exception e) {
            dbLoadBalancer.setStatus(CoreLoadBalancerStatus.ERROR);
            loadBalancerRepository.update(dbLoadBalancer);
            String alertDescription = String.format("Error removing nodes '%s' in LB Device for loadbalancer '%d'.", nodesToDeleteIdString, msg.getLoadBalancerId());
            logger.error(alertDescription, e);
            notificationService.saveAlert(msg.getAccountId(), msg.getLoadBalancerId(), e, LBDEVICE_FAILURE.name(), alertDescription);
            sendErrorToEventResource(msg, msg.getIds());
            return;
        }

        try {
            // Removes node from load balancer in DB
            logger.debug(String.format("Removing nodes '[%s]' from load balancer '%d' in LB Device...", nodesToDeleteIdString, msg.getLoadBalancerId()));
            nodeRepository.deleteNodes(dbLoadBalancer, new HashSet<Integer>(msg.getIds()));
            logger.debug(String.format("Succesfully removed nodes '[%s]' from load balancer '%d' in LB Device...", nodesToDeleteIdString, msg.getLoadBalancerId()));
        } catch (Exception ex) {
            logger.debug(String.format("Error removing nodes '[%s]' from load balancer '%d' in LB Device...", nodesToDeleteIdString, msg.getLoadBalancerId()));
        }

        // Update load balancer status in DB
        loadBalancerRepository.changeStatus(dbLoadBalancer, CoreLoadBalancerStatus.ACTIVE);

        // Add atom entry
        String atomTitle = "Nodes Successfully Deleted";
        String atomSummary = "Nodes successfully deleted";
        for (Integer doomedNodeId : msg.getIds()) {
            notificationService.saveNodeEvent(msg.getUserName(), msg.getAccountId(), msg.getLoadBalancerId(), doomedNodeId, atomTitle, atomSummary, DELETE_NODE, DELETE, INFO);
        }
        logger.info(String.format("Delete node operation complete for load balancer '%d'.", msg.getLoadBalancerId()));
    }

    private void sendErrorToEventResource(MessageDataContainer msg) {
        String title = "Error Deleting Node";
        String desc = "Could not delete the node at this time.";
        notificationService.saveLoadBalancerEvent(msg.getUserName(), msg.getAccountId(), msg.getLoadBalancerId(), title, desc, DELETE_NODE, DELETE, CRITICAL);
    }

    private void sendErrorToEventResource(MessageDataContainer msg, List<Integer> nodeIds) {
        String title = "Error Deleting Nodes";
        String desc = "Could not delete the nodes at this time.";
        for (Integer nodeId : nodeIds) {
            notificationService.saveNodeEvent(msg.getUserName(), msg.getAccountId(), msg.getAccountId(), nodeId, title, desc, DELETE_NODE, DELETE, CRITICAL);
        }
    }
}
