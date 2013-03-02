package org.daylight.pathweaver.api.async;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.daylight.pathweaver.datamodel.CoreLoadBalancerStatus;
import org.daylight.pathweaver.datamodel.CoreUsageEventType;
import org.daylight.pathweaver.service.domain.entity.LoadBalancer;
import org.daylight.pathweaver.service.domain.exception.EntityNotFoundException;
import org.daylight.pathweaver.service.domain.repository.LoadBalancerRepository;
import org.daylight.pathweaver.service.domain.service.LoadBalancerService;
import org.daylight.pathweaver.service.domain.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;

import static org.daylight.pathweaver.service.domain.common.AlertType.DATABASE_FAILURE;
import static org.daylight.pathweaver.service.domain.common.AlertType.LBDEVICE_FAILURE;
import static org.daylight.pathweaver.service.domain.event.entity.CategoryType.DELETE;
import static org.daylight.pathweaver.service.domain.event.entity.EventSeverity.CRITICAL;
import static org.daylight.pathweaver.service.domain.event.entity.EventSeverity.INFO;
import static org.daylight.pathweaver.service.domain.event.entity.EventType.DELETE_LOADBALANCER;

@Component
public class DeleteLoadBalancerListener extends BaseListener {
    private final Log logger = LogFactory.getLog(DeleteLoadBalancerListener.class);

    @Autowired
    private LoadBalancerService loadBalancerService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private LoadBalancerRepository loadBalancerRepository;

    @Override
    public void doOnMessage(final Message message) throws Exception {
        logger.debug("Entering " + getClass());
        logger.debug(message);

        LoadBalancer queueLb = getDataContainerFromMessage(message).getLoadBalancer();
        LoadBalancer dbLoadBalancer;

        try {
            dbLoadBalancer = loadBalancerRepository.getByIdAndAccountId(queueLb.getId(), queueLb.getAccountId());
        } catch (EntityNotFoundException enfe) {
            String alertDescription = String.format("Load balancer '%d' not found in database.", queueLb.getId());
            logger.error(alertDescription, enfe);
            notificationService.saveAlert(queueLb.getAccountId(), queueLb.getId(), enfe, DATABASE_FAILURE.name(), alertDescription);
            sendErrorToEventResource(queueLb);
            return;
        }

        // If This loadBalancer has never been created successfully on the adapter, so
        // there is no delete needed on the adapter.
        if (dbLoadBalancer.isCreatedOnAdapter())    {
            try {
                logger.debug(String.format("Deleting load balancer '%d' in LB Device...", dbLoadBalancer.getId()));
                getReverseProxyLoadBalancerService().deleteLoadBalancer(dbLoadBalancer);
                logger.debug(String.format("Successfully deleted load balancer '%d' in LB Device.", dbLoadBalancer.getId()));
            } catch (Exception e) {
                loadBalancerRepository.changeStatus(dbLoadBalancer, CoreLoadBalancerStatus.ERROR);
                logger.error(String.format("LoadBalancer status before error was: '%s'", dbLoadBalancer.getStatus()));
                String alertDescription = String.format("Error deleting loadbalancer '%d' in LB Device.", dbLoadBalancer.getId());
                logger.error(alertDescription, e);
                notificationService.saveAlert(dbLoadBalancer.getAccountId(), dbLoadBalancer.getId(), e, LBDEVICE_FAILURE.name(), alertDescription);
                sendErrorToEventResource(queueLb);
                return;
            }
        }

        loadBalancerService.delete(dbLoadBalancer);

        // Add atom entry
        String atomTitle = "Load Balancer Successfully Deleted";
        String atomSummary = "Load balancer successfully deleted";
        notificationService.saveLoadBalancerEvent(queueLb.getUserName(), dbLoadBalancer.getAccountId(), dbLoadBalancer.getId(), atomTitle, atomSummary, DELETE_LOADBALANCER, DELETE, INFO);

        // Notify usage processor with a usage event
        notifyUsageProcessor(message, dbLoadBalancer, CoreUsageEventType.DELETE_LOAD_BALANCER);

        logger.info(String.format("Load balancer '%d' successfully deleted.", dbLoadBalancer.getId()));
    }

    private void sendErrorToEventResource(LoadBalancer lb) {
        String title = "Error Deleting Load Balancer";
        String desc = "Could not delete the load balancer at this time.";
        notificationService.saveLoadBalancerEvent(lb.getUserName(), lb.getAccountId(), lb.getId(), title, desc, DELETE_LOADBALANCER, DELETE, CRITICAL);
    }

}
