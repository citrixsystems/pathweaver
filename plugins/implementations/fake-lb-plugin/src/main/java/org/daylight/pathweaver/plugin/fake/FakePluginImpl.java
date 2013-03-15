package org.daylight.pathweaver.plugin.fake;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.daylight.pathweaver.plugin.LoadBalancerPluginBase;


import org.daylight.pathweaver.plugin.common.entity.LoadBalancerHost;
import org.daylight.pathweaver.plugin.exception.PluginException;
import org.daylight.pathweaver.common.config.Configuration;
import org.daylight.pathweaver.service.domain.entity.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class FakePluginImpl extends LoadBalancerPluginBase {

    private static Log logger = LogFactory.getLog(FakePluginImpl.class.getName());


    @Autowired
    public FakePluginImpl(Configuration configuration) {

        super(configuration);
    }


    @Override
    public void doCreateLoadBalancer(LoadBalancer lb, LoadBalancerHost lbHost) throws PluginException {

        // Now we can create the load balancer on the remote device

       //String serviceUrl = lbHost.getHost().getEndpoint();

       logger.info("createLoadBalancer"); // NOP

    }

    @Override
    public void doUpdateLoadBalancer(LoadBalancer lb, LoadBalancerHost lbHost) throws PluginException {


        //String serviceUrl = lbHost.getHost().getEndpoint();

        logger.info("updateLoadBalancer");// NOP

    }

    @Override
    public void doDeleteLoadBalancer(LoadBalancer lb, LoadBalancerHost lbHost) throws PluginException {

        //String serviceUrl = lbHost.getHost().getEndpoint();

        logger.info("deleteLoadBalancer");// NOP

    }

    @Override
    public void doCreateNodes(Integer accountId, Integer lbId, Set<Node> nodes, LoadBalancerHost lbHost) throws PluginException {

        //String serviceUrl = lbHost.getHost().getEndpoint();

        logger.info("createNodes");// NOP
    }

    @Override
    public void doDeleteNodes(Integer accountId, Integer lbId, Set<Node> nodes, LoadBalancerHost lbHost) throws PluginException {

        //String serviceUrl = lbHost.getHost().getEndpoint();

        logger.info("deleteNodes");// NOP
    }

    @Override
    public void doUpdateNode(Integer accountId, Integer lbId, Node node, LoadBalancerHost lbHost) throws PluginException {

        //String serviceUrl = lbHost.getHost().getEndpoint();

        logger.info("updateNodes");// NOP
    }

    @Override
    public void doUpdateConnectionThrottle(Integer accountId, Integer lbId, ConnectionThrottle connectionThrottle, LoadBalancerHost lbHost) throws PluginException {

        //String serviceUrl = lbHost.getHost().getEndpoint();

        logger.info("updateConnectionThrottle");// NOP
    }

    @Override
    public void doDeleteConnectionThrottle(Integer accountId, Integer lbId, LoadBalancerHost lbHost) throws PluginException {

        //String serviceUrl = lbHost.getHost().getEndpoint();

        logger.info("deleteConnectionThrottle");// NOP
    }

    @Override
    public void doUpdateHealthMonitor(Integer accountId, Integer lbId, HealthMonitor monitor, LoadBalancerHost lbHost) throws PluginException {

        //String serviceUrl = lbHost.getHost().getEndpoint();

        logger.info("updateHealthMonitor");// NOP
    }

    @Override
    public void doDeleteHealthMonitor(Integer accountId, Integer lbId, LoadBalancerHost lbHost) throws PluginException {

        //String serviceUrl = lbHost.getHost().getEndpoint();

        logger.info("deleteHealthMonitor");// NOP
    }

    @Override
    public void doSetSessionPersistence(Integer accountId, Integer lbId, SessionPersistence sessionPersistence, LoadBalancerHost lbHost) throws PluginException {

        //String serviceUrl = lbHost.getHost().getEndpoint();

        logger.info("setSessionPersistence");// NOP
    }

    @Override
    public void doDeleteSessionPersistence(Integer accountId, Integer lbId, LoadBalancerHost lbHost) throws PluginException {
        //String serviceUrl = lbHost.getHost().getEndpoint();

        logger.info("deleteSessionPersistence");// NOP
    }
}
