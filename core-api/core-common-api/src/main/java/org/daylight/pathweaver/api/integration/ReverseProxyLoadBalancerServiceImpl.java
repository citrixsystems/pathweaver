package org.daylight.pathweaver.api.integration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.daylight.pathweaver.adapter.LoadBalancerAdapter;

import org.daylight.pathweaver.adapter.exception.AdapterException;
import org.daylight.pathweaver.adapter.exception.ConnectionException;
import org.daylight.pathweaver.api.config.PublicApiServiceConfigurationKeys;
import org.daylight.pathweaver.common.config.Configuration;
import org.daylight.pathweaver.common.crypto.CryptoUtil;
import org.daylight.pathweaver.common.crypto.exception.DecryptException;
import org.daylight.pathweaver.service.domain.entity.*;
import org.daylight.pathweaver.service.domain.exception.EntityNotFoundException;
import org.daylight.pathweaver.service.domain.repository.LoadBalancerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ReverseProxyLoadBalancerServiceImpl implements ReverseProxyLoadBalancerService {
    private final Log logger = LogFactory.getLog(ReverseProxyLoadBalancerServiceImpl.class);

    @Autowired
    private Configuration configuration;

    @Autowired
    private LoadBalancerAdapter loadBalancerAdapter;
    @Autowired
    private LoadBalancerRepository loadBalancerRepository;

    @Override
    public void createLoadBalancer(Integer accountId, LoadBalancer lb) throws AdapterException, DecryptException, MalformedURLException  {

        if (configuration != null) {
            logger.debug("Configuration is not null");
        } else {
            logger.debug("Configuration is null");
        }

        loadBalancerAdapter.createLoadBalancer(lb);
    }

    @Override
    public void updateLoadBalancer(Integer accountId, LoadBalancer lb) throws AdapterException, DecryptException, MalformedURLException  {
        loadBalancerAdapter.updateLoadBalancer(lb);
    }

    public void deleteLoadBalancer(LoadBalancer lb) throws AdapterException, DecryptException, MalformedURLException  {
        loadBalancerAdapter.deleteLoadBalancer(lb);
    }

    @Override
    public void createNodes(Integer accountId, Integer lbId, Set<Node> nodes) throws AdapterException, DecryptException, MalformedURLException {
        loadBalancerAdapter.createNodes(accountId, lbId, nodes);
    }

    @Override
    public void deleteNodes(Integer accountId, Integer lbId, Set<Node> nodes) throws AdapterException, DecryptException, MalformedURLException {
        loadBalancerAdapter.deleteNodes(accountId, lbId, nodes);
    }

    @Override
    public void updateNode(Integer accountId, Integer lbId, Node node) throws AdapterException, DecryptException, MalformedURLException {
        loadBalancerAdapter.updateNode(accountId, lbId, node);
    }

    @Override
    public void deleteNode(Integer accountId, Integer lbId, Node node) throws AdapterException, DecryptException, MalformedURLException {

        Set<Node> nodes = new HashSet<Node>();
        nodes.add(node);
        loadBalancerAdapter.deleteNodes(accountId, lbId, nodes);
    }

    @Override
    public void updateConnectionThrottle(Integer accountId, Integer lbId, ConnectionThrottle connectionThrottle) throws AdapterException, DecryptException, MalformedURLException {
        loadBalancerAdapter.updateConnectionThrottle(accountId, lbId, connectionThrottle);
    }

    @Override
    public void deleteConnectionThrottle(Integer accountId, Integer lbId) throws AdapterException, DecryptException, MalformedURLException {
        loadBalancerAdapter.deleteConnectionThrottle(accountId, lbId);
    }

    @Override
    public void updateHealthMonitor(Integer accountId, Integer lbId, HealthMonitor monitor) throws AdapterException, DecryptException, MalformedURLException {
        loadBalancerAdapter.updateHealthMonitor(accountId, lbId, monitor);
    }

    @Override
    public void deleteHealthMonitor(Integer accountId, Integer lbId) throws AdapterException, DecryptException, MalformedURLException {
        loadBalancerAdapter.deleteHealthMonitor(accountId, lbId);
    }

    @Override
    public void setSessionPersistence(Integer lbId, Integer accountId, SessionPersistence sessionPersistence) throws ConnectionException, AdapterException, MalformedURLException  {
        loadBalancerAdapter.setSessionPersistence(accountId, lbId, sessionPersistence);
    }

    @Override
    public void deleteSessionPersistence(Integer accountId, Integer lbId) throws AdapterException, DecryptException, MalformedURLException {
        loadBalancerAdapter.deleteSessionPersistence(accountId, lbId);
    }


}
