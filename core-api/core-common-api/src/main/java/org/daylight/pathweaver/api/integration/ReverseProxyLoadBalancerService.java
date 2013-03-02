package org.daylight.pathweaver.api.integration;

import org.daylight.pathweaver.service.domain.entity.*;
import org.daylight.pathweaver.adapter.exception.AdapterException;
import org.daylight.pathweaver.adapter.exception.ConnectionException;
import org.daylight.pathweaver.common.crypto.exception.DecryptException;
import java.net.MalformedURLException;

import java.util.Set;

public interface ReverseProxyLoadBalancerService {

    void createLoadBalancer(Integer accountId, LoadBalancer lb)  throws AdapterException, DecryptException, MalformedURLException ;

    void updateLoadBalancer(Integer accountId, LoadBalancer lb) throws AdapterException, DecryptException, MalformedURLException ;

    void deleteLoadBalancer(LoadBalancer lb) throws AdapterException, DecryptException, MalformedURLException ;

    void createNodes(Integer accountId, Integer lbId, Set<Node> nodes) throws AdapterException, DecryptException, MalformedURLException ;

    void deleteNodes(Integer accountId, Integer lbId, Set<Node> nodes) throws AdapterException, DecryptException, MalformedURLException ;

    void updateNode(Integer accountId, Integer lbId, Node node) throws AdapterException, DecryptException, MalformedURLException ;

    void deleteNode(Integer accountId, Integer lbId, Node node) throws AdapterException, DecryptException, MalformedURLException ;
 
    void updateConnectionThrottle(Integer accountId, Integer lbId, ConnectionThrottle connectionThrottle) throws AdapterException, DecryptException, MalformedURLException ;

    void deleteConnectionThrottle(Integer accountId, Integer lbId) throws AdapterException, DecryptException, MalformedURLException ;

    void updateHealthMonitor(Integer accountId, Integer lbId, HealthMonitor monitor) throws AdapterException, DecryptException, MalformedURLException ;

    void deleteHealthMonitor(Integer accountId, Integer lbId) throws AdapterException, DecryptException, MalformedURLException ;

    void setSessionPersistence(Integer lbId, Integer accountId, SessionPersistence sessionPersistence) throws AdapterException, DecryptException, MalformedURLException ;

    void deleteSessionPersistence(Integer accountId, Integer lbId) throws AdapterException, DecryptException, MalformedURLException ;
}
