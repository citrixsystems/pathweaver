package org.daylight.pathweaver.adapter.common.service;

import org.daylight.pathweaver.adapter.common.entity.VirtualIpv4;
import org.daylight.pathweaver.service.domain.entity.LoadBalancer;
import org.daylight.pathweaver.service.domain.exception.PersistenceServiceException;

public interface AdapterVirtualIpService {
    LoadBalancer assignVipsToLoadBalancer(LoadBalancer loadBalancer) throws PersistenceServiceException;
    VirtualIpv4 createVirtualIpCluster(VirtualIpv4 vipCluster) throws PersistenceServiceException;
    VirtualIpv4 getVirtualIpCluster(Integer vipId);
    void removeAllVipsFromLoadBalancer(LoadBalancer lb);
    void undoAllVipsFromLoadBalancer(LoadBalancer lb);
}