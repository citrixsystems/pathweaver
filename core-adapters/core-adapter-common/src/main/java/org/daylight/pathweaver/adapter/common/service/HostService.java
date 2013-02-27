package org.daylight.pathweaver.adapter.common.service;

import org.daylight.pathweaver.adapter.common.entity.Host;
import org.daylight.pathweaver.adapter.common.entity.LoadBalancerHost;
import org.daylight.pathweaver.service.domain.exception.PersistenceServiceException;

public interface HostService {
    Host getDefaultActiveHost();
    LoadBalancerHost createLoadBalancerHost(LoadBalancerHost lbHost) throws PersistenceServiceException;
    void removeLoadBalancerHost(LoadBalancerHost lbHost) throws PersistenceServiceException;
    LoadBalancerHost getLoadBalancerHost(Integer loadBalancerId);
}
