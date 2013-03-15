package org.daylight.pathweaver.service.domain.service;

import org.daylight.pathweaver.service.domain.entity.LoadBalancer;
import org.daylight.pathweaver.service.domain.exception.EntityNotFoundException;
import org.daylight.pathweaver.service.domain.exception.PersistenceServiceException;

import java.util.List;

public interface LoadBalancerService {
    LoadBalancer create(LoadBalancer loadBalancer) throws PersistenceServiceException;

    LoadBalancer update(LoadBalancer loadBalancer) throws PersistenceServiceException;

    void preDelete(Integer accountId, List<Integer> loadBalancerIds) throws PersistenceServiceException;

    void preDelete(Integer accountId, Integer loadBalancerId) throws PersistenceServiceException;

    void delete(LoadBalancer lb) throws PersistenceServiceException;
}
