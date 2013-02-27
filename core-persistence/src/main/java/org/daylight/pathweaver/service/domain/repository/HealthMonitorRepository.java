package org.daylight.pathweaver.service.domain.repository;

import org.daylight.pathweaver.service.domain.entity.HealthMonitor;
import org.daylight.pathweaver.service.domain.exception.EntityNotFoundException;

public interface HealthMonitorRepository {
    HealthMonitor getByLoadBalancerId(Integer loadBalancerId) throws EntityNotFoundException;

    void delete(HealthMonitor healthMonitor) throws EntityNotFoundException;
}
