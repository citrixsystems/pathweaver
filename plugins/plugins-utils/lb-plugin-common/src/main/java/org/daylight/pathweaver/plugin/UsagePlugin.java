package org.daylight.pathweaver.plugin;

import org.daylight.pathweaver.plugin.exception.PluginException;
import org.daylight.pathweaver.service.domain.entity.LoadBalancer;

import java.util.List;
import java.util.Map;

public interface UsagePlugin {

    Map<Integer, Long> getTransferBytesIn(List<LoadBalancer> lbs) throws PluginException;

    Map<Integer, Long> getTransferBytesOut(List<LoadBalancer> lbs) throws PluginException;
}
