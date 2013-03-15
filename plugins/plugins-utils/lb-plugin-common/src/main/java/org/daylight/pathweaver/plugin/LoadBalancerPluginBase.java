package org.daylight.pathweaver.plugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.daylight.pathweaver.plugin.common.config.LoadBalancerEndpointConfiguration;
import org.daylight.pathweaver.plugin.common.config.PublicApiServiceConfigurationKeys;
import org.daylight.pathweaver.plugin.common.entity.Cluster;
import org.daylight.pathweaver.plugin.common.entity.Host;
import org.daylight.pathweaver.plugin.common.entity.LoadBalancerHost;
import org.daylight.pathweaver.plugin.common.repository.HostRepository;
import org.daylight.pathweaver.plugin.common.service.PluginVirtualIpService;
import org.daylight.pathweaver.plugin.common.service.HostService;
import org.daylight.pathweaver.plugin.exception.PluginException;
import org.daylight.pathweaver.common.config.Configuration;
import org.daylight.pathweaver.common.crypto.CryptoUtil;
import org.daylight.pathweaver.common.crypto.exception.DecryptException;
import org.daylight.pathweaver.service.domain.entity.*;
import org.daylight.pathweaver.service.domain.exception.PersistenceServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;




@Service
public abstract class LoadBalancerPluginBase implements LoadBalancerPlugin {

    private static Log logger = LogFactory.getLog(LoadBalancerPluginBase.class.getName());

    @Autowired
    private HostService hostService;


    @Autowired
    private PluginVirtualIpService virtualIpService;


    private String logFileLocation;

    private String pluginConfigFileLocation;

    // Derived classes must implement the following methods
    protected abstract void doCreateLoadBalancer(LoadBalancer lb, LoadBalancerHost lbHost)  throws PluginException;
    protected abstract void doUpdateLoadBalancer(LoadBalancer lb, LoadBalancerHost lbHost)  throws PluginException;
    protected abstract void doDeleteLoadBalancer(LoadBalancer lb, LoadBalancerHost lbHost)  throws PluginException;
    protected abstract void doCreateNodes(Integer accountId, Integer lbId, Set<Node> nodes, LoadBalancerHost lbHost) throws PluginException;
    protected abstract void doDeleteNodes(Integer accountId, Integer lbId, Set<Node> nodes, LoadBalancerHost lbHost) throws PluginException;
    protected abstract void doUpdateNode(Integer accountId, Integer lbId, Node node, LoadBalancerHost lbHost) throws PluginException;
    protected abstract void doUpdateConnectionThrottle(Integer accountId, Integer lbId, ConnectionThrottle connectionThrottle, LoadBalancerHost lbHost)  throws PluginException;;
    protected abstract void doDeleteConnectionThrottle(Integer accountId, Integer lbId, LoadBalancerHost lbHost) throws PluginException;
    protected abstract void doUpdateHealthMonitor(Integer accountId, Integer lbId, HealthMonitor monitor, LoadBalancerHost lbHost) throws PluginException;
    protected abstract void doDeleteHealthMonitor(Integer accountId, Integer lbId, LoadBalancerHost lbHost) throws PluginException;
    protected abstract void doSetSessionPersistence(Integer accountId, Integer lbId, SessionPersistence sessionPersistence, LoadBalancerHost lbHost) throws PluginException;
    protected abstract void doDeleteSessionPersistence(Integer accountId, Integer lbId, LoadBalancerHost lbHost) throws PluginException;

    public LoadBalancerPluginBase(Configuration configuration) {

        logFileLocation = configuration.getString(PublicApiServiceConfigurationKeys.access_log_file_location);
        pluginConfigFileLocation = configuration.getString(PublicApiServiceConfigurationKeys.plugin_config_file_location);

        //Read settings from our plugin config file.
    }



    private LoadBalancerHost  getLoadBalancerHost(Integer  lbId)
    {
        if (hostService == null) {
            logger.debug("hostService is null !");
        }

        LoadBalancerHost lbHost = hostService.getLoadBalancerHost(lbId);

        return lbHost;
    }



    private void undoCreateLoadBalancer(LoadBalancer lb, LoadBalancerHost lbHost) {

        try {
            hostService.removeLoadBalancerHost(lbHost);
            virtualIpService.undoAllVipsFromLoadBalancer(lb);
        } catch (PersistenceServiceException e) {
            logger.error(String.format("Failed to remove LoadBalancerHost for lbId %d: %s", lb.getId(), e.getMessage()));
        }
    }

    private void removeLoadBalancerPluginResources(LoadBalancer lb) {

        try {
            LoadBalancerHost lbHost = hostService.getLoadBalancerHost(lb.getId());
            hostService.removeLoadBalancerHost(lbHost);
            virtualIpService.removeAllVipsFromLoadBalancer(lb);
        } catch (PersistenceServiceException e) {
            logger.error(String.format("Failed to remove LoadBalancerHost for lbId %d: %s", lb.getId(), e.getMessage()));
        }
    }



    @Override
    public void createLoadBalancer(LoadBalancer lb) throws PluginException {

        // Choose a host for this new load Balancer
        Host host = hostService.getDefaultActiveHost();

        if (host == null)  {
            throw new PluginException("Cannot retrieve default active host from persistence layer");
        }

        LoadBalancerHost lbHost = new LoadBalancerHost(lb.getId(), host);


        try {
            logger.debug("Before calling hostService.createLoadBalancerHost()");
            hostService.createLoadBalancerHost(lbHost);
            // Also assign the Virtual IP for this load balancer
            virtualIpService.assignVipsToLoadBalancer(lb);
        } catch (PersistenceServiceException e) {
            throw new PluginException("Cannot assign Vips to the loadBalancer : " + e.getMessage(), e);
        }


         try {
             // call derived class to do the real job
            doCreateLoadBalancer(lb, lbHost);
        } catch (Exception e) {
            // Undo creation on plugin of this loadbalancer if there is an error.
            undoCreateLoadBalancer(lb, lbHost);
            throw new PluginException("Error occurred while creating request or connecting to device : " + e.getMessage(), e);
        }
    }




    @Override
    public void updateLoadBalancer(LoadBalancer lb) throws PluginException {

        LoadBalancerHost lbHost = getLoadBalancerHost(lb.getId());

        // call derived class to do the real job
        doUpdateLoadBalancer(lb, lbHost);
    }

    @Override
    public void deleteLoadBalancer(LoadBalancer lb) throws PluginException {

        LoadBalancerHost lbHost = getLoadBalancerHost(lb.getId());

        // call derived class to do the real job
        doDeleteLoadBalancer(lb, lbHost);

        // Cleanup the state of the plugin for this load balancer
        removeLoadBalancerPluginResources(lb);
    }

    @Override
    public void createNodes(Integer accountId, Integer lbId, Set<Node> nodes) throws PluginException {

        LoadBalancerHost lbHost = getLoadBalancerHost(lbId);

        // call derived class to do the real job
        doCreateNodes(accountId, lbId, nodes, lbHost);

    }

    @Override
    public void deleteNodes(Integer accountId, Integer lbId, Set<Node> nodes) throws PluginException {

        LoadBalancerHost lbHost = getLoadBalancerHost(lbId);

        // call derived class to do the real job
        doDeleteNodes(accountId, lbId, nodes, lbHost);
    }

    @Override
    public void updateNode(Integer accountId, Integer lbId, Node node) throws PluginException {

        LoadBalancerHost lbHost = getLoadBalancerHost(lbId);

        // call derived class to do the real job
        doUpdateNode(accountId, lbId, node, lbHost);
    }

    @Override
    public void updateConnectionThrottle(Integer accountId, Integer lbId, ConnectionThrottle connectionThrottle) throws PluginException {

        LoadBalancerHost lbHost = getLoadBalancerHost(lbId);

        // call derived class to do the real job
        doUpdateConnectionThrottle(accountId, lbId, connectionThrottle, lbHost);
    }

    @Override
    public void deleteConnectionThrottle(Integer accountId, Integer lbId) throws PluginException {

        LoadBalancerHost lbHost = getLoadBalancerHost(lbId);

        // call derived class to do the real job
        doDeleteConnectionThrottle(accountId, lbId, lbHost);
    }

    @Override
    public void updateHealthMonitor(Integer accountId, Integer lbId, HealthMonitor monitor) throws PluginException {

        LoadBalancerHost lbHost = getLoadBalancerHost(lbId);

        // call derived class to do the real job
        doUpdateHealthMonitor(accountId, lbId, monitor, lbHost);
    }

    @Override
    public void deleteHealthMonitor(Integer accountId, Integer lbId) throws PluginException {

        LoadBalancerHost lbHost = getLoadBalancerHost(lbId);

        // call derived class to do the real job
        doDeleteHealthMonitor(accountId, lbId, lbHost);
    }

    @Override
    public void setSessionPersistence(Integer accountId, Integer lbId, SessionPersistence sessionPersistence) throws PluginException {

        LoadBalancerHost lbHost = getLoadBalancerHost(lbId);

        // call derived class to do the real job
        doSetSessionPersistence(accountId, lbId, sessionPersistence, lbHost);
    }

    @Override
    public void deleteSessionPersistence(Integer accountId, Integer lbId) throws PluginException {


        LoadBalancerHost lbHost = getLoadBalancerHost(lbId);

        // call derived class to do the real job
        doDeleteSessionPersistence(accountId, lbId, lbHost);
    }
}
