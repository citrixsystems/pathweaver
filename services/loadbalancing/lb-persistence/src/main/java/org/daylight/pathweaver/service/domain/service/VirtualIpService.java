package org.daylight.pathweaver.service.domain.service;


import org.daylight.pathweaver.service.domain.entity.LoadBalancer;
import org.daylight.pathweaver.service.domain.exception.PersistenceServiceException;

import java.security.NoSuchAlgorithmException;

public interface VirtualIpService {
    LoadBalancer assignVipsToLoadBalancer(LoadBalancer loadBalancer) throws PersistenceServiceException;

    void addAccountRecord(Integer accountId) throws NoSuchAlgorithmException;

    void updateLoadBalancerVips(LoadBalancer lb)  throws PersistenceServiceException ;

    void removeAllVipsFromLoadBalancer(LoadBalancer lb);

}
