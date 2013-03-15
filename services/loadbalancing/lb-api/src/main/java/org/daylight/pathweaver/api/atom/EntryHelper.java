package org.daylight.pathweaver.api.atom;

import org.daylight.pathweaver.common.ip.exception.IPStringConversionException1;
import org.daylight.pathweaver.datamodel.CoreHealthMonitorType;
import org.daylight.pathweaver.service.domain.entity.*;

public final class EntryHelper {
    public static final String CREATE_NODE_TITLE = "Node Successfully Created";
    public static final String CREATE_VIP_TITLE = "Virtual Ip Successfully Added";
    public static final String UPDATE_MONITOR_TITLE = "Health Monitor Successfully Updated";
    public static final String UPDATE_PERSISTENCE_TITLE = "Session Persistence Successfully Updated";
    public static final String UPDATE_loggerGING_TITLE = "Connection Logging Successfully Updated";
    public static final String UPDATE_THROTTLE_TITLE = "Connection Throttle Successfully Updated";
    public static final String UPDATE_ACCESS_LIST_TITLE = "Access List Successfully Updated";

    public static final String ATTRIBUTE_SEPARATOR = "', ";

    public static String createNodeSummary(Node node) {
        StringBuffer atomSummary = new StringBuffer();
        atomSummary.append("Node successfully created with ");
        atomSummary.append("address: '").append(node.getAddress()).append(ATTRIBUTE_SEPARATOR);
        atomSummary.append("port: '").append(node.getPort()).append(ATTRIBUTE_SEPARATOR);
        atomSummary.append("enabled: '").append(node.isEnabled()).append(ATTRIBUTE_SEPARATOR);
        atomSummary.append("weight: '").append(node.getWeight()).append(ATTRIBUTE_SEPARATOR);
        return atomSummary.toString();
    }

    public static String createVirtualIpSummary(VirtualIp virtualIp) {
        StringBuffer atomSummary = new StringBuffer();
        atomSummary.append("Virtual ip successfully added with ");
        atomSummary.append("address: '").append(virtualIp.getAddress()).append(ATTRIBUTE_SEPARATOR);
        atomSummary.append("type: '").append(virtualIp.getVipType()).append(ATTRIBUTE_SEPARATOR);
        return atomSummary.toString();
    }

    public static String createVirtualIpSummary() {
        return "Virtual ip successfully added";
    }

    public static String createHealthMonitorSummary(LoadBalancer lb) {
        StringBuffer atomSummary = new StringBuffer();
        atomSummary.append("Health monitor successfully updated with ");
        atomSummary.append("type: '").append(lb.getHealthMonitor().getType()).append(ATTRIBUTE_SEPARATOR);
        atomSummary.append("delay: '").append(lb.getHealthMonitor().getDelay()).append(ATTRIBUTE_SEPARATOR);
        atomSummary.append("timeout: '").append(lb.getHealthMonitor().getTimeout()).append(ATTRIBUTE_SEPARATOR);
        atomSummary.append("attemptsBeforeDeactivation: '").append(lb.getHealthMonitor().getAttemptsBeforeDeactivation());

        if (lb.getHealthMonitor().getType().equals(CoreHealthMonitorType.CONNECT)) {
            atomSummary.append("'");
        } else {
            atomSummary.append("', ");
            atomSummary.append("path: '").append(lb.getHealthMonitor().getPath()).append("'");
        }

        return atomSummary.toString();
    }

    public static String createConnectionThrottleSummary(LoadBalancer lb) {
        StringBuffer atomSummary = new StringBuffer();
        atomSummary.append("Connection throttle successfully updated with ");
        atomSummary.append("maxRequestRate: '").append(lb.getConnectionThrottle().getMaxRequestRate()).append(ATTRIBUTE_SEPARATOR);
        atomSummary.append("rateInterval: '").append(lb.getConnectionThrottle().getRateInterval()).append("'");
        return atomSummary.toString();
    }
}
