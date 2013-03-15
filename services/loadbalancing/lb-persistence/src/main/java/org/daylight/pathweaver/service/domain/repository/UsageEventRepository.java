package org.daylight.pathweaver.service.domain.repository;

import org.daylight.pathweaver.service.domain.entity.UsageEventRecord;

import java.util.List;

public interface UsageEventRepository {
    List<UsageEventRecord> getAllUsageEventEntries();

    void batchCreate(List<UsageEventRecord> usages);

    void batchDelete(List<UsageEventRecord> usages);
}
