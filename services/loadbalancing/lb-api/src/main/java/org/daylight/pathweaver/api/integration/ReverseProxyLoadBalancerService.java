package org.daylight.pathweaver.api.integration;

import org.daylight.pathweaver.service.domain.entity.*;
import org.daylight.pathweaver.plugin.exception.PluginException;
import org.daylight.pathweaver.plugin.exception.ConnectionException;
import org.daylight.pathweaver.common.crypto.exception.DecryptException;
import java.net.MalformedURLException;

import java.util.Set;

public interface ReverseProxyLoadBalancerService {

    void createLoadBalancer(Integer accountId, LoadBalancer lb)  throws PluginException, DecryptException, MalformedURLException ;

    void updateLoadBalancer(Integer accountId, LoadBalancer lb) throws PluginException, DecryptException, MalformedURLException ;

    void deleteLoadBalancer(LoadBalancer lb) throws PluginException, DecryptException, MalformedURLException ;

    void createNodes(Integer accountId, Integer lbId, Set<Node> nodes) throws PluginException, DecryptException, MalformedURLException ;

    void deleteNodes(Integer accountId, Integer lbId, Set<Node> nodes) throws PluginException, DecryptException, MalformedURLException ;

    void updateNode(Integer accountId, Integer lbId, Node node) throws PluginException, DecryptException, MalformedURLException ;

    void deleteNode(Integer accountId, Integer lbId, Node node) throws PluginException, DecryptException, MalformedURLException ;
 
    void updateConnectionThrottle(Integer accountId, Integer lbId, ConnectionThrottle connectionThrottle) throws PluginException, DecryptException, MalformedURLException ;

    void deleteConnectionThrottle(Integer accountId, Integer lbId) throws PluginException, DecryptException, MalformedURLException ;

    void updateHealthMonitor(Integer accountId, Integer lbId, HealthMonitor monitor) throws PluginException, DecryptException, MalformedURLException ;

    void deleteHealthMonitor(Integer accountId, Integer lbId) throws PluginException, DecryptException, MalformedURLException ;

    void setSessionPersistence(Integer lbId, Integer accountId, SessionPersistence sessionPersistence) throws PluginException, DecryptException, MalformedURLException ;

    void deleteSessionPersistence(Integer accountId, Integer lbId) throws PluginException, DecryptException, MalformedURLException ;
}
