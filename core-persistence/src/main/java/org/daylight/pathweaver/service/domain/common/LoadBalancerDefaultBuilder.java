package org.daylight.pathweaver.service.domain.common;

import org.daylight.pathweaver.datamodel.CoreLoadBalancerStatus;
import org.daylight.pathweaver.datamodel.CoreNodeStatus;
import org.daylight.pathweaver.service.domain.entity.*;

public class LoadBalancerDefaultBuilder {


    private LoadBalancerDefaultBuilder() {
    }

    public static LoadBalancer addDefaultValues(final LoadBalancer loadBalancer) {
        loadBalancer.setStatus(CoreLoadBalancerStatus.QUEUED);
        NodesHelper.setNodesToStatus(loadBalancer, CoreNodeStatus.ONLINE);

        return loadBalancer;
    }
}
