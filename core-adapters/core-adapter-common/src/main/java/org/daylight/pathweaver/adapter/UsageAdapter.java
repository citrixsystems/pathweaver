package org.daylight.pathweaver.adapter;

import org.daylight.pathweaver.adapter.exception.AdapterException;
import org.daylight.pathweaver.service.domain.entity.LoadBalancer;

import java.util.List;
import java.util.Map;

public interface UsageAdapter {

    Map<Integer, Long> getTransferBytesIn(List<LoadBalancer> lbs) throws AdapterException;

    Map<Integer, Long> getTransferBytesOut(List<LoadBalancer> lbs) throws AdapterException;
}
