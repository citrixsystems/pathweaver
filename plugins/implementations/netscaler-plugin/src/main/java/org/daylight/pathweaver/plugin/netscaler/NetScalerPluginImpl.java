package org.daylight.pathweaver.plugin.netscaler;


import org.daylight.pathweaver.common.config.Configuration;

import org.daylight.pathweaver.service.domain.entity.*;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import org.daylight.pathweaver.plugin.common.entity.LoadBalancerHost;

import org.daylight.pathweaver.plugin.LoadBalancerPluginBase;
import org.daylight.pathweaver.plugin.exception.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NetScalerPluginImpl extends LoadBalancerPluginBase {
    private static Log logger = LogFactory.getLog(NetScalerPluginImpl.class.getName());



    @Autowired
    private NSPluginUtils nsPluginUtils;



    @Autowired
    public NetScalerPluginImpl(Configuration configuration) {

        super(configuration);

        //Read settings from our plugin config file.
    }


    @Override
    public void doCreateLoadBalancer(LoadBalancer lb, LoadBalancerHost lbHost)
            throws PluginException {


        String resourceType = "loadbalancers";

        Integer accountId = lb.getAccountId();

        com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.LoadBalancer nsLB = new com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.LoadBalancer();

        nsPluginUtils.populateNSLoadBalancerForCreate(lb, nsLB);

        String requestBody = nsPluginUtils.getRequestBody(nsLB);

        String serviceUrl = lbHost.getHost().getEndpoint();

        String resourceUrl = nsPluginUtils.getLBURLStr(serviceUrl, accountId, resourceType);

        nsPluginUtils.performRequest("POST", resourceUrl, requestBody);


    }


    @Override
    public void doUpdateLoadBalancer(LoadBalancer lb, LoadBalancerHost lbHost)
        throws PluginException
    {

        String serviceUrl = lbHost.getHost().getEndpoint();

        String resourceType = "loadbalancers";
        Integer resourceId = lb.getId();

        Integer accountId = lb.getAccountId();

        com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.LoadBalancer nsLB = new com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.LoadBalancer();
        nsPluginUtils.populateNSLoadBalancerForUpdate(lb, nsLB);

        String requestBody = nsPluginUtils.getRequestBody(nsLB);

        String resourceUrl = nsPluginUtils.getLBURLStr(serviceUrl, accountId, resourceType, resourceId);

        nsPluginUtils.performRequest("PUT", resourceUrl, requestBody);
    }


    @Override
    public void doDeleteLoadBalancer(LoadBalancer lb, LoadBalancerHost lbHost)
            throws PluginException
    {

        String serviceUrl = lbHost.getHost().getEndpoint();

        String resourceType = "loadbalancers";
        
        Integer accountId = lb.getAccountId(); 
        Integer lbId = lb.getId();   

        logger.debug("NetScaler plugin preparing to delete loadbalancer with id " + lbId);

        String resourceUrl = nsPluginUtils.getLBURLStr(serviceUrl, accountId, resourceType, lbId);

        nsPluginUtils.performRequest("DELETE", resourceUrl);

    }

    @Override
    public void doCreateNodes(Integer accountId, Integer lbId, Set<Node> nodes, LoadBalancerHost lbHost)
        throws PluginException
    {

        String serviceUrl = lbHost.getHost().getEndpoint();

        String resourceType = "loadbalancers";
        Integer resourceId = lbId;
        String childResourceType = "nodes";

        if(nodes.size() > 0)
        {
            com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.Nodes nsNodes = new com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.Nodes();
            nsPluginUtils.populateNSNodes(nodes, nsNodes.getNodes());
            String requestBody = nsPluginUtils.getRequestBody(nsNodes);

            String resourceUrl = nsPluginUtils.getLBURLStr(serviceUrl, accountId, resourceType, resourceId, childResourceType);

            nsPluginUtils.performRequest("PUT", resourceUrl, requestBody);
        }
    }
    

    @Override
    public void doDeleteNodes(Integer accountId, Integer lbId, Set<Node> nodes, LoadBalancerHost lbHost)
        throws PluginException
    {

    	for(Node node: nodes)
    	{
			this.doRemoveNode(lbId, accountId, node.getId(), lbHost);
    	}    
    }
    

    @Override
    public void doUpdateNode(Integer accountId, Integer lbId, Node node, LoadBalancerHost lbHost)
        throws PluginException
    {
        
        String serviceUrl = lbHost.getHost().getEndpoint();

        String resourceType = "loadbalancers";
        Integer resourceId = lbId;
        String childResourceType = "nodes";

        com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.Node nsNode = new com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.Node();
		nsPluginUtils.populateNSNode(node, nsNode);
        String requestBody = nsPluginUtils.getRequestBody(nsNode);

		String resourceUrl = nsPluginUtils.getLBURLStr(serviceUrl, accountId, resourceType, resourceId,childResourceType) + "/" + node.getId();
		
		nsPluginUtils.performRequest("PUT", resourceUrl, requestBody);
    }

    
    @Override
    public void doUpdateConnectionThrottle(Integer accountId, Integer lbId, ConnectionThrottle conThrottle, LoadBalancerHost lbHost)
        throws PluginException
    {

        String serviceUrl = lbHost.getHost().getEndpoint();

        String resourceType = "loadbalancers";
        Integer resourceId = lbId;
        String childResourceType = "connectionthrottle";
		
        com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.ConnectionThrottle nsThrottle = new com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.ConnectionThrottle();
		nsPluginUtils.populateConnectionThrottle(conThrottle, nsThrottle);
        String requestBody = nsPluginUtils.getRequestBody(nsThrottle);
        String resourceUrl = nsPluginUtils.getLBURLStr(serviceUrl, accountId, resourceType, resourceId, childResourceType);

        nsPluginUtils.performRequest("PUT", resourceUrl, requestBody);
    }

    @Override
    public void doDeleteConnectionThrottle(Integer accountId, Integer lbId, LoadBalancerHost lbHost)
        throws PluginException
    {

        String serviceUrl = lbHost.getHost().getEndpoint();

        String resourceType = "loadbalancers";
        Integer resourceId = lbId;
        String childResourceType = "connectionthrottle";

        String resourceUrl = nsPluginUtils.getLBURLStr(serviceUrl, accountId, resourceType, resourceId, childResourceType);

        nsPluginUtils.performRequest("DELETE", resourceUrl);
    }

    @Override
    public void doUpdateHealthMonitor(Integer accountId, Integer lbId, HealthMonitor monitor, LoadBalancerHost lbHost)
        throws PluginException
    {

        String serviceUrl = lbHost.getHost().getEndpoint();

        String resourceType = "loadbalancers";
        Integer resourceId = lbId;
        String childResourceType = "healthmonitor";

        com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.HealthMonitor nsMon;

        nsMon  = new com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.HealthMonitor();  

        nsPluginUtils.populateNSHealthMonitor(monitor, nsMon);

        String requestBody = nsPluginUtils.getRequestBody(nsMon);
        String resourceUrl = nsPluginUtils.getLBURLStr(serviceUrl, accountId, resourceType, resourceId, childResourceType);

        nsPluginUtils.performRequest("PUT", resourceUrl, requestBody);
    }

    @Override
    public void doDeleteHealthMonitor(Integer accountId, Integer lbId, LoadBalancerHost lbHost)
        throws PluginException
    {

        String serviceUrl = lbHost.getHost().getEndpoint();

        String resourceType = "loadbalancers";
        Integer resourceId = lbId;
        String childResourceType = "healthmonitor";

        String resourceUrl = nsPluginUtils.getLBURLStr(serviceUrl, accountId, resourceType, resourceId, childResourceType);

        nsPluginUtils.performRequest("DELETE", resourceUrl);
    }

    @Override
    public void doSetSessionPersistence(Integer accountId, Integer lbId, SessionPersistence sessionPersistence, LoadBalancerHost lbHost)
        throws PluginException
    {

        String serviceUrl = lbHost.getHost().getEndpoint();

        String resourceType = "loadbalancers";
        Integer resourceId = lbId;
        String childResourceType = "sessionpersistence";


        com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.SessionPersistence nsPersistence = new com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.SessionPersistence();
		nsPluginUtils.populateSessionPersistence(sessionPersistence, nsPersistence);
        String requestBody = nsPluginUtils.getRequestBody(nsPersistence);

        String resourceUrl = nsPluginUtils.getLBURLStr(serviceUrl, accountId, resourceType, resourceId, childResourceType);

        nsPluginUtils.performRequest("PUT", resourceUrl, requestBody);
    }

    @Override
    public void doDeleteSessionPersistence(Integer accountId, Integer lbId, LoadBalancerHost lbHost)
        throws PluginException
    {
        String serviceUrl = lbHost.getHost().getEndpoint();

        String resourceType = "loadbalancers";
        Integer resourceId = lbId;
        String childResourceType = "sessionpersistence";


        String resourceUrl = nsPluginUtils.getLBURLStr(serviceUrl, accountId, resourceType, resourceId, childResourceType);

        nsPluginUtils.performRequest("DELETE", resourceUrl, "");
    }


    private void doRemoveNode(Integer lbId, Integer accountId, Integer nodeId, LoadBalancerHost lbHost)
            throws PluginException
    {

        String serviceUrl = lbHost.getHost().getEndpoint();

        String resourceType = "loadbalancers";
        Integer resourceId = lbId;
        String childResourceType = "nodes";

		String resourceUrl = nsPluginUtils.getLBURLStr(serviceUrl, accountId, resourceType, resourceId,childResourceType) + "/" + nodeId;
		
		nsPluginUtils.performRequest("DELETE", resourceUrl);
    }
}

