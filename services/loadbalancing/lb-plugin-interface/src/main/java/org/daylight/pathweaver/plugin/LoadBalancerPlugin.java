package org.daylight.pathweaver.plugin;

import org.daylight.pathweaver.plugin.exception.PluginException;
import org.daylight.pathweaver.service.domain.entity.LoadBalancer;
import org.daylight.pathweaver.service.domain.entity.Node;
import org.daylight.pathweaver.service.domain.entity.ConnectionThrottle;
import org.daylight.pathweaver.service.domain.entity.HealthMonitor;
import org.daylight.pathweaver.service.domain.entity.SessionPersistence;

import java.util.Set;

public interface LoadBalancerPlugin {

    void createLoadBalancer(LoadBalancer lb) throws PluginException;

    void updateLoadBalancer(LoadBalancer lb) throws PluginException;

    void deleteLoadBalancer(LoadBalancer lb) throws PluginException;

    void createNodes(Integer accountId, Integer lbId, Set<Node> nodes) throws PluginException;

    void deleteNodes(Integer accountId, Integer lbId, Set<Node> nodes) throws PluginException;

    void updateNode( Integer accountId, Integer lbId, Node node) throws PluginException;

    void updateConnectionThrottle(Integer accountId, Integer lbId, ConnectionThrottle connectionThrottle) throws PluginException;

    void deleteConnectionThrottle(Integer accountId, Integer lbId) throws PluginException;

    void updateHealthMonitor(Integer accountId, Integer lbId, HealthMonitor healthMonitor) throws PluginException;

    void deleteHealthMonitor(Integer accountId, Integer lbId) throws PluginException;

    void setSessionPersistence(Integer accountId, Integer lbId, SessionPersistence sessionPersistence) throws PluginException;

    void deleteSessionPersistence(Integer accountId, Integer lbId) throws PluginException;
}
