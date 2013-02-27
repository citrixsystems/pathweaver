package org.daylight.pathweaver.api.async;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.daylight.pathweaver.api.atom.EntryHelper;
import org.daylight.pathweaver.api.helper.NodesHelper;
import org.daylight.pathweaver.datamodel.CoreNodeStatus;
import org.daylight.pathweaver.datamodel.CoreUsageEventType;
import org.daylight.pathweaver.service.domain.entity.*;
import org.daylight.pathweaver.service.domain.event.entity.EventType;
import org.daylight.pathweaver.service.domain.exception.EntityNotFoundException;
import org.daylight.pathweaver.service.domain.pojo.MessageDataContainer;
import org.daylight.pathweaver.service.domain.repository.LoadBalancerRepository;
import org.daylight.pathweaver.service.domain.service.LoadBalancerService;
import org.daylight.pathweaver.service.domain.service.VirtualIpService;
import org.daylight.pathweaver.service.domain.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;

import static org.daylight.pathweaver.api.atom.EntryHelper.*;
import static org.daylight.pathweaver.datamodel.CoreLoadBalancerStatus.ACTIVE;
import static org.daylight.pathweaver.datamodel.CoreLoadBalancerStatus.ERROR;
import static org.daylight.pathweaver.service.domain.common.AlertType.DATABASE_FAILURE;
import static org.daylight.pathweaver.service.domain.common.AlertType.LBDEVICE_FAILURE;
import static org.daylight.pathweaver.service.domain.event.entity.CategoryType.CREATE;
import static org.daylight.pathweaver.service.domain.event.entity.CategoryType.UPDATE;
import static org.daylight.pathweaver.service.domain.event.entity.EventSeverity.CRITICAL;
import static org.daylight.pathweaver.service.domain.event.entity.EventSeverity.INFO;
import static org.daylight.pathweaver.service.domain.event.entity.EventType.*;


@Component
public class CreateLoadBalancerListener extends BaseListener {
    private final Log LOG = LogFactory.getLog(CreateLoadBalancerListener.class);

    @Autowired
    private LoadBalancerService loadBalancerService;


    @Autowired
    private VirtualIpService virtualIpService;

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private LoadBalancerRepository loadBalancerRepository;

    @Override
    public void doOnMessage(final Message message) throws Exception {
        Integer lbid = null;
        Integer accountId = null;
        LoadBalancer dbLoadBalancer = null;


        LOG.debug("Entering " + getClass());
        LOG.debug(message);

        MessageDataContainer dataContainer = getDataContainerFromMessage(message);

        LoadBalancer queueLb = dataContainer.getLoadBalancer();

        try {
            lbid = queueLb.getId();
            accountId = queueLb.getAccountId();

            dbLoadBalancer = loadBalancerRepository.getByIdAndAccountId(lbid, accountId);
        } catch (EntityNotFoundException e) {
            LOG.error("Error retrieving loadbalancer from DB");
            String alertDescription = String.format("Load balancer '%d' not found in database.", queueLb.getId());
            LOG.error(alertDescription, e);
            notificationService.saveAlert(accountId, lbid, e, DATABASE_FAILURE.name(), alertDescription);
            sendErrorToEventResource(dbLoadBalancer);
            return;
        }

        try {
            LOG.debug(String.format("Creating load balancer '%d' via adapter...", lbid));
            reverseProxyLoadBalancerService.createLoadBalancer(accountId, dbLoadBalancer);
            LOG.debug("Successfully created a load balancer via adapter.");
        } catch (Exception e) {
            LOG.error(e.getMessage());
            dbLoadBalancer.setStatus(ERROR);
            dbLoadBalancer.setCreatedOnAdapter(false);
            virtualIpService.removeAllVipsFromLoadBalancer(dbLoadBalancer);
            NodesHelper.setNodesToStatus(dbLoadBalancer, CoreNodeStatus.OFFLINE);
            loadBalancerRepository.update(dbLoadBalancer);
            String alertDescription = String.format("An error occurred while creating loadbalancer '%d' via adapter.", dbLoadBalancer.getId());
            LOG.error(alertDescription, e);
            notificationService.saveAlert(dbLoadBalancer.getAccountId(), dbLoadBalancer.getId(), e, LBDEVICE_FAILURE.name(), alertDescription);
            sendErrorToEventResource(dbLoadBalancer);
            return;
        }




        // Update load balancer in DB
        virtualIpService.updateLoadBalancerVips(dbLoadBalancer);
        dbLoadBalancer.setStatus(ACTIVE);
        dbLoadBalancer.setCreatedOnAdapter(true);
        NodesHelper.setNodesToStatus(dbLoadBalancer, CoreNodeStatus.ONLINE);
        dbLoadBalancer = loadBalancerRepository.update(dbLoadBalancer);

        addAtomEntryForLoadBalancer(dbLoadBalancer, dbLoadBalancer);
        addAtomEntriesForNodes(dbLoadBalancer, dbLoadBalancer);
        addAtomEntriesForVips(dbLoadBalancer, dbLoadBalancer);
        addAtomEntryForHealthMonitor(dbLoadBalancer, dbLoadBalancer);
        addAtomEntryForConnectionThrottle(dbLoadBalancer, dbLoadBalancer);

        // Notify usage processor
        notifyUsageProcessor(message, dbLoadBalancer, CoreUsageEventType.CREATE_LOAD_BALANCER);

        LOG.info(String.format("Successfully created load balancer '%d'.", dbLoadBalancer.getId()));
    }

    private void addAtomEntryForConnectionThrottle(LoadBalancer queueLb, org.daylight.pathweaver.service.domain.entity.LoadBalancer dbLoadBalancer) {
        if (dbLoadBalancer.getConnectionThrottle() != null) {
            notificationService.saveConnectionThrottleEvent(queueLb.getUserName(), dbLoadBalancer.getAccountId(), dbLoadBalancer.getId(), dbLoadBalancer.getConnectionThrottle().getId(), UPDATE_THROTTLE_TITLE, EntryHelper.createConnectionThrottleSummary(dbLoadBalancer), SET_CONNECTION_THROTTLE, UPDATE, INFO);
        }
    }

    private void addAtomEntryForHealthMonitor(LoadBalancer queueLb, org.daylight.pathweaver.service.domain.entity.LoadBalancer dbLoadBalancer) {
        if (dbLoadBalancer.getHealthMonitor() != null) {
            notificationService.saveHealthMonitorEvent(queueLb.getUserName(), dbLoadBalancer.getAccountId(), dbLoadBalancer.getId(), dbLoadBalancer.getHealthMonitor().getId(), UPDATE_MONITOR_TITLE, EntryHelper.createHealthMonitorSummary(dbLoadBalancer), SET_HEALTH_MONITOR, UPDATE, INFO);
        }
    }

    private void addAtomEntriesForVips(LoadBalancer queueLb, org.daylight.pathweaver.service.domain.entity.LoadBalancer dbLoadBalancer) {
        for (LoadBalancerJoinVip loadBalancerJoinVip : dbLoadBalancer.getLoadBalancerJoinVipSet()) {
            VirtualIp vip = loadBalancerJoinVip.getVirtualIp();
            notificationService.saveVirtualIpEvent(queueLb.getUserName(), dbLoadBalancer.getAccountId(), dbLoadBalancer.getId(), vip.getId(), CREATE_VIP_TITLE, EntryHelper.createVirtualIpSummary(vip), EventType.ADD_VIRTUAL_IP, CREATE, INFO);
        }
    }

    private void addAtomEntriesForNodes(LoadBalancer queueLb, org.daylight.pathweaver.service.domain.entity.LoadBalancer dbLoadBalancer) {
        for (Node node : dbLoadBalancer.getNodes()) {

            notificationService.saveNodeEvent(queueLb.getUserName(), dbLoadBalancer.getAccountId(), dbLoadBalancer.getId(),
                    node.getId(), CREATE_NODE_TITLE, EntryHelper.createNodeSummary(node), CREATE_NODE, CREATE, INFO);
        }
    }

    private void addAtomEntryForLoadBalancer(LoadBalancer queueLb, org.daylight.pathweaver.service.domain.entity.LoadBalancer dbLoadBalancer) {
        String atomTitle = "Load Balancer Successfully Created";
        String atomSummary = createAtomSummary(dbLoadBalancer).toString();
        notificationService.saveLoadBalancerEvent(queueLb.getUserName(), dbLoadBalancer.getAccountId(), dbLoadBalancer.getId(), atomTitle, atomSummary, CREATE_LOADBALANCER, CREATE, INFO);
    }

    private void sendErrorToEventResource(LoadBalancer lb) {
        String title = "Error Creating Load Balancer";
        String desc = "Could not create a load balancer at this time";
        notificationService.saveLoadBalancerEvent(lb.getUserName(), lb.getAccountId(), lb.getId(), title, desc, CREATE_LOADBALANCER, CREATE, CRITICAL);
    }

    private StringBuffer createAtomSummary(org.daylight.pathweaver.service.domain.entity.LoadBalancer lb) {
        StringBuffer atomSummary = new StringBuffer();
        atomSummary.append("Load balancer successfully created with ");
        atomSummary.append("name: '").append(lb.getName()).append("', ");
        atomSummary.append("algorithm: '").append(lb.getAlgorithm()).append("', ");
        atomSummary.append("protocol: '").append(lb.getProtocol()).append("', ");
        atomSummary.append("port: '").append(lb.getPort()).append("'");
        return atomSummary;
    }
}
