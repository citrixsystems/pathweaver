package org.daylight.pathweaver.service.domain.repository;

import org.daylight.pathweaver.service.domain.entity.UsageRecord;
import org.daylight.pathweaver.service.domain.exception.EntityNotFoundException;

import java.util.Collection;
import java.util.List;

public interface UsageRepository {
    List<UsageRecord> getByLoadBalancerId(Integer loadBalancerId) throws EntityNotFoundException;

    List<UsageRecord> getMostRecentUsageRecordsForLoadBalancers(Collection<Integer> lbIds);

    UsageRecord getMostRecentUsageForLoadBalancer(Integer loadBalancerId);

    void batchCreate(List<UsageRecord> recordsToInsert);

    void batchUpdate(List<UsageRecord> recordsToUpdate);
}
