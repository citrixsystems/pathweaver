package org.daylight.pathweaver.service.domain.service;

import org.daylight.pathweaver.service.domain.exception.PersistenceServiceException;

public interface ExtraFeatureService<T> {
    T update(Integer loadBalancerId, T objectToUpdate) throws PersistenceServiceException;

    void preDelete(Integer loadBalancerId) throws PersistenceServiceException;

    void delete(Integer loadBalancerId) throws PersistenceServiceException;
}
