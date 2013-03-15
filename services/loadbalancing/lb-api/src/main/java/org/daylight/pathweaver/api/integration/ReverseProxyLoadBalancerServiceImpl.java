package org.daylight.pathweaver.api.integration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.daylight.pathweaver.plugin.LoadBalancerPlugin;

import org.daylight.pathweaver.plugin.exception.PluginException;
import org.daylight.pathweaver.plugin.exception.ConnectionException;
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
    private LoadBalancerPlugin loadBalancerPlugin;
    @Autowired
    private LoadBalancerRepository loadBalancerRepository;

    @Override
    public void createLoadBalancer(Integer accountId, LoadBalancer lb) throws PluginException, DecryptException, MalformedURLException  {

        if (configuration != null) {
            logger.debug("Configuration is not null");
        } else {
            logger.debug("Configuration is null");
        }

        loadBalancerPlugin.createLoadBalancer(lb);
    }

    @Override
    public void updateLoadBalancer(Integer accountId, LoadBalancer lb) throws PluginException, DecryptException, MalformedURLException  {
        loadBalancerPlugin.updateLoadBalancer(lb);
    }

    public void deleteLoadBalancer(LoadBalancer lb) throws PluginException, DecryptException, MalformedURLException  {
        loadBalancerPlugin.deleteLoadBalancer(lb);
    }

    @Override
    public void createNodes(Integer accountId, Integer lbId, Set<Node> nodes) throws PluginException, DecryptException, MalformedURLException {
        loadBalancerPlugin.createNodes(accountId, lbId, nodes);
    }

    @Override
    public void deleteNodes(Integer accountId, Integer lbId, Set<Node> nodes) throws PluginException, DecryptException, MalformedURLException {
        loadBalancerPlugin.deleteNodes(accountId, lbId, nodes);
    }

    @Override
    public void updateNode(Integer accountId, Integer lbId, Node node) throws PluginException, DecryptException, MalformedURLException {
        loadBalancerPlugin.updateNode(accountId, lbId, node);
    }

    @Override
    public void deleteNode(Integer accountId, Integer lbId, Node node) throws PluginException, DecryptException, MalformedURLException {

        Set<Node> nodes = new HashSet<Node>();
        nodes.add(node);
        loadBalancerPlugin.deleteNodes(accountId, lbId, nodes);
    }

    @Override
    public void updateConnectionThrottle(Integer accountId, Integer lbId, ConnectionThrottle connectionThrottle) throws PluginException, DecryptException, MalformedURLException {
        loadBalancerPlugin.updateConnectionThrottle(accountId, lbId, connectionThrottle);
    }

    @Override
    public void deleteConnectionThrottle(Integer accountId, Integer lbId) throws PluginException, DecryptException, MalformedURLException {
        loadBalancerPlugin.deleteConnectionThrottle(accountId, lbId);
    }

    @Override
    public void updateHealthMonitor(Integer accountId, Integer lbId, HealthMonitor monitor) throws PluginException, DecryptException, MalformedURLException {
        loadBalancerPlugin.updateHealthMonitor(accountId, lbId, monitor);
    }

    @Override
    public void deleteHealthMonitor(Integer accountId, Integer lbId) throws PluginException, DecryptException, MalformedURLException {
        loadBalancerPlugin.deleteHealthMonitor(accountId, lbId);
    }

    @Override
    public void setSessionPersistence(Integer lbId, Integer accountId, SessionPersistence sessionPersistence) throws ConnectionException, PluginException, MalformedURLException  {
        loadBalancerPlugin.setSessionPersistence(accountId, lbId, sessionPersistence);
    }

    @Override
    public void deleteSessionPersistence(Integer accountId, Integer lbId) throws PluginException, DecryptException, MalformedURLException {
        loadBalancerPlugin.deleteSessionPersistence(accountId, lbId);
    }


}
