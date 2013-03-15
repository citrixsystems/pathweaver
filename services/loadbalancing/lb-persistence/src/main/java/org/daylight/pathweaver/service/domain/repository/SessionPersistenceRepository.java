package org.daylight.pathweaver.service.domain.repository;

import org.daylight.pathweaver.service.domain.entity.SessionPersistence;
import org.daylight.pathweaver.service.domain.exception.EntityNotFoundException;

public interface SessionPersistenceRepository {
    SessionPersistence getByLoadBalancerId(Integer loadBalancerId) throws EntityNotFoundException;

    void delete(SessionPersistence sessionPersistence) throws EntityNotFoundException;
}
