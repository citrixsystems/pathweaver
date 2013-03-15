package org.daylight.pathweaver.plugin.common.service;

import org.daylight.pathweaver.plugin.common.entity.VirtualIpv4;
import org.daylight.pathweaver.service.domain.entity.LoadBalancer;
import org.daylight.pathweaver.service.domain.exception.PersistenceServiceException;

public interface PluginVirtualIpService {
    LoadBalancer assignVipsToLoadBalancer(LoadBalancer loadBalancer) throws PersistenceServiceException;
    VirtualIpv4 createVirtualIpCluster(VirtualIpv4 vipCluster) throws PersistenceServiceException;
    VirtualIpv4 getVirtualIpCluster(Integer vipId);
    void removeAllVipsFromLoadBalancer(LoadBalancer lb);
    void undoAllVipsFromLoadBalancer(LoadBalancer lb);
}