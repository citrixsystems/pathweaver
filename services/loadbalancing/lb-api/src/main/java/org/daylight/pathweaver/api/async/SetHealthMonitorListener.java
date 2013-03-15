package org.daylight.pathweaver.api.async;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.daylight.pathweaver.api.atom.EntryHelper;
import org.daylight.pathweaver.datamodel.CoreLoadBalancerStatus;
import org.daylight.pathweaver.service.domain.entity.LoadBalancer;
import org.daylight.pathweaver.service.domain.exception.EntityNotFoundException;
import org.daylight.pathweaver.service.domain.pojo.MessageDataContainer;
import org.daylight.pathweaver.service.domain.repository.LoadBalancerRepository;
import org.daylight.pathweaver.service.domain.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;

import static org.daylight.pathweaver.api.atom.EntryHelper.UPDATE_MONITOR_TITLE;
import static org.daylight.pathweaver.api.helper.AlertType.DATABASE_FAILURE;
import static org.daylight.pathweaver.api.helper.AlertType.LBDEVICE_FAILURE;
import static org.daylight.pathweaver.service.domain.event.entity.CategoryType.UPDATE;
import static org.daylight.pathweaver.service.domain.event.entity.EventSeverity.CRITICAL;
import static org.daylight.pathweaver.service.domain.event.entity.EventSeverity.INFO;
import static org.daylight.pathweaver.service.domain.event.entity.EventType.SET_HEALTH_MONITOR;

@Component
public class SetHealthMonitorListener extends BaseListener {
    private final Log logger = LogFactory.getLog(SetHealthMonitorListener.class);

    @Autowired
    private LoadBalancerRepository loadBalancerRepository;
    @Autowired
    private NotificationService notificationService;

    @Override
    public void doOnMessage(final Message message) throws Exception {
        MessageDataContainer dataContainer = getDataContainerFromMessage(message);
        LoadBalancer queueLb = dataContainer.getLoadBalancer();
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

        try {
            logger.debug(String.format("Updating health monitor for load balancer '%d' in LB Device...", dbLoadBalancer.getId()));
            getReverseProxyLoadBalancerService().updateHealthMonitor(dbLoadBalancer.getAccountId(), dbLoadBalancer.getId(), dbLoadBalancer.getHealthMonitor());
            logger.debug(String.format("Successfully updated health monitor for load balancer '%d' in LB Device...", dbLoadBalancer.getId()));
        } catch (Exception e) {
            loadBalancerRepository.changeStatus(dbLoadBalancer, CoreLoadBalancerStatus.ERROR);
            String alertDescription = String.format("Error updating health monitor in LB Device for loadbalancer '%d'.", dbLoadBalancer.getId());
            logger.error(alertDescription, e);
            notificationService.saveAlert(dbLoadBalancer.getAccountId(), dbLoadBalancer.getId(), e, LBDEVICE_FAILURE.name(), alertDescription);
            sendErrorToEventResource(queueLb);
            return;
        }

        // Update load balancer status in DB
        loadBalancerRepository.changeStatus(dbLoadBalancer, CoreLoadBalancerStatus.ACTIVE);

        // Add atom entry
        notificationService.saveHealthMonitorEvent(queueLb.getUserName(), dbLoadBalancer.getAccountId(), dbLoadBalancer.getId(), dbLoadBalancer.getHealthMonitor().getId(), UPDATE_MONITOR_TITLE, EntryHelper.createHealthMonitorSummary(dbLoadBalancer), SET_HEALTH_MONITOR, UPDATE, INFO);

        logger.info(String.format("Update health monitor operation complete for load balancer '%d'.", dbLoadBalancer.getId()));
    }

    private void sendErrorToEventResource(LoadBalancer lb) {
        String title = "Error Updating Health Monitor";
        String desc = "Could not update the health monitor settings at this time.";
        notificationService.saveHealthMonitorEvent(lb.getUserName(), lb.getAccountId(), lb.getId(), lb.getHealthMonitor().getId(), title, desc, SET_HEALTH_MONITOR, UPDATE, CRITICAL);
    }
}
