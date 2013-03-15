package org.daylight.pathweaver.service.domain.repository;

import org.daylight.pathweaver.service.domain.entity.ConnectionThrottle;
import org.daylight.pathweaver.service.domain.exception.EntityNotFoundException;

public interface ConnectionThrottleRepository {
    ConnectionThrottle getByLoadBalancerId(Integer loadBalancerId) throws EntityNotFoundException;

    void delete(ConnectionThrottle connectionThrottle) throws EntityNotFoundException;
}
