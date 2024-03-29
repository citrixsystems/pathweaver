package org.daylight.pathweaver.plugin.netscaler;

import java.util.*;
import java.io.*;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.daylight.pathweaver.plugin.common.config.LoadBalancerEndpointConfiguration;
import org.daylight.pathweaver.plugin.common.service.PluginVirtualIpService;
import org.daylight.pathweaver.plugin.exception.*;
import org.daylight.pathweaver.service.domain.entity.*;
import org.daylight.pathweaver.service.domain.entity.ConnectionThrottle;
import org.daylight.pathweaver.service.domain.entity.HealthMonitor;
import org.daylight.pathweaver.service.domain.entity.LoadBalancer;
import org.daylight.pathweaver.service.domain.entity.Node;
import org.daylight.pathweaver.service.domain.entity.SessionPersistence;
import org.daylight.pathweaver.service.domain.entity.VirtualIp;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class NSPluginUtils
{
    private static Log logger = LogFactory.getLog(NSPluginUtils.class.getName());

    @Autowired 
    private PluginVirtualIpService pluginVirtualIpService;

    public String getLBURLStr(String serviceUrl, Integer accountId, String resourceType)
    {
    	String resourceUrl = serviceUrl + "/" + accountId + "/" + resourceType;

        return resourceUrl;
    }


    public String getLBURLStr(String serviceUrl, Integer accountId, String resourceType, Integer resourceId)
    {
    	String resourceUrl = getLBURLStr(serviceUrl, accountId, resourceType) + "/" + resourceId;

        return resourceUrl;
    }


    public String getLBURLStr(String serviceUrl, Integer accountId, String resourceType, Integer resourceId, String childResourceType)
    {
    	String resourceUrl = getLBURLStr(serviceUrl, accountId, resourceType, resourceId) + "/" + childResourceType;

        return resourceUrl;
    }


    public String getLBURLStr(String serviceUrl, Integer accountId, String resourceType, Integer resourceId, String childResourceType, Integer childResourceId)
    {
    	String resourceUrl = getLBURLStr(serviceUrl, accountId, resourceType, resourceId, childResourceType) + "/" + childResourceId;

        return resourceUrl;
    }

    
    public String performRequest(String method, String urlStr, String requestBody)
           throws PluginException
    {
        logger.debug(String.format("Service URL string: '%s'...", urlStr));
		
        logger.debug(String.format("Load balancer request " + new Throwable().getStackTrace()[1].getMethodName() + ": '%s'...", requestBody));
	
        Map<String, String> headers = new HashMap<String, String>();
		
        headers.put("Content-Type", "application/xml");
        headers.put("Accept", "application/xml");
        headers.put("X-Auth-Token", "tk82848ebd-f079-4959-bbd3-6c7e27ea4d9a");
		
        try 
        {
            return NSRequest.perform_request(method, urlStr, headers, requestBody);
        } 
        catch (Exception e)
        {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();
            
            logger.debug(e.getMessage());
            logger.debug(stacktrace);
            throw new PluginException("Exception occurred: " + e.getMessage(), e);
        }
    }


    public void performRequest(String method, String urlStr)
           throws PluginException
    {
        performRequest(method, urlStr, null);
    }


    public Object getResponseObject(String response)
           throws PluginException
    {
	 	
        try 
        {
			JAXBContext ctxt = JAXBContext.newInstance("com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1");
			Unmarshaller  u = ctxt.createUnmarshaller() ; 
			return u.unmarshal( new StreamSource( new StringReader( response) ) );
		} 
        catch (JAXBException e) 
        {
			logger.debug("error: " + e.toString());;
			throw new PluginException("Failed to transform a XML Payload to JAXB object", e);
		}
	}


    public String getRequestBody(Object marshalObject)
           throws PluginException
    {
        String requestBody;
	 	
        try 
        {
            JAXBContext ctxt = JAXBContext.newInstance("com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1");
            Marshaller m = ctxt.createMarshaller() ; 
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            Writer writer = new StringWriter();
			
            m.marshal(marshalObject, writer);
	        requestBody = writer.toString();
	
	        logger.debug("request body: " + requestBody);
	    } 
        catch (JAXBException e) 
        {
	        logger.error("Failed during JAXB-> XML conversion : ", e);
	        throw new PluginException("Failed to transform a JAXB object to XML payload...", e);
	    }

    	return requestBody;
    }

    public void populateNSLoadBalancerForUpdate(LoadBalancer lb, com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.LoadBalancer nsLB)
           throws BadRequestException
    {
    	String name = lb.getName();
    	String alg = lb.getAlgorithm().toString();

    	nsLB.setName(name);
        nsLB.setAlgorithm(alg);

        //Set all the lists to null so they won't get generated in the request payload.
        nsLB.setVirtualIp(null);
        nsLB.setNodes(null);
        nsLB.setCertificates(null);
    }

    public void populateNSLoadBalancerForCreate(LoadBalancer lb, com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.LoadBalancer nsLB)
           throws BadRequestException
    {
        com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.VirtualIp nsVIP = new com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.VirtualIp();
        com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.HealthMonitor nsMon = new com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.HealthMonitor();
        com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.SessionPersistence nsPersistence = new com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.SessionPersistence();
        com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.ConnectionThrottle nsThrottle = new com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.ConnectionThrottle();


        Integer id = lb.getId();
    	String name = lb.getName();
    	String alg = lb.getAlgorithm().toString();
    	String prot = lb.getProtocol().toString();
    	Integer port = lb.getPort();
        Set<LoadBalancerJoinVip> vips = lb.getLoadBalancerJoinVipSet();
    	Set<Node> nodes = lb.getNodes();
    	HealthMonitor monitor = lb.getHealthMonitor();
		SessionPersistence sp = lb.getSessionPersistence();
		ConnectionThrottle throttle = lb.getConnectionThrottle();

        // If we find an IPv6 address we use that one. If not we use an IPv4 address

        populateNSVIP(vips, nsVIP);

        
        if (nsVIP.getId() <= 0)
        {
            throw new BadRequestException("VirtualIP element of loadbalancer missing....", new Error());
        }     
        
        populateNSNodes(nodes, nsLB.getNodes().getNodes());
        
		if(sp != null)
		{
			populateSessionPersistence(lb.getSessionPersistence(), nsPersistence);
		}
		else
		{
			nsPersistence = null;
		}

    	if (monitor != null) 
    	{
            populateNSHealthMonitor(monitor, nsMon);
        } else {
            nsMon = null; 
        }

    	if (throttle != null) 
    	{
            populateConnectionThrottle(throttle, nsThrottle);
        } else {
            nsThrottle = null; 
        }

        nsLB.setId(id);
    	nsLB.setName(name);
        nsLB.setPort(port);
        nsLB.setProtocol(prot);
        nsLB.setAlgorithm(alg);
        nsLB.setVirtualIp(nsVIP);

        if ((nsLB.getNodes() == null) || (nsLB.getNodes().getNodes().size() == 0)) {
            nsLB.setNodes(null);
        }

        nsLB.setSessionPersistence(nsPersistence);   
        nsLB.setHealthMonitor(nsMon);   
		nsLB.setConnectionThrottle(nsThrottle);
    }

    public void populateNSVIP(Set<LoadBalancerJoinVip> vips, com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.VirtualIp nsVIP)
           throws BadRequestException
                 
    {
        if (vips == null)
        {
            nsVIP.setId(-1);
            return;
        }
            
        if (vips.size() > 1)
        {
            throw new BadRequestException("Core plugins can support only one VIP per loadbalancer", new Error());
        }
        
        for (LoadBalancerJoinVip lbjoinVip : vips)
        {
            VirtualIp vip = lbjoinVip.getVirtualIp();

            nsVIP.setId(vip.getId());

    	    nsVIP.setAddress(vip.getAddress());

            nsVIP.setIpVersion(com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.IpVersion.fromValue(vip.getIpVersion().name()));

            nsVIP.setType(com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.VirtualIpType.fromValue(vip.getVipType().name()));

            break; // process only the first Virtual IP of a loadbalancer
        }
    }

    
    private void translateNode(Node node, com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.Node nsNode, boolean forUpdate)
           throws BadRequestException
    {


	   Integer nodeid = node.getId();
	   String address = node.getAddress();
	   Integer port = node.getPort();
	   Boolean enabled = node.isEnabled();
	   Integer weight = node.getWeight();

	   if ((address == null) || (port == null))
	   {
		   throw new BadRequestException("Missing attributes [ipAddress, port] from node element....", new Error());
	   }

	   logger.debug(String.format("node %d: address:%s, port:%d ", nodeid, address, port));

	   if(!forUpdate)
	   {
		   nsNode.setId(nodeid);
		   nsNode.setAddress(address);
		   nsNode.setPort(port);
	   }
	   nsNode.setWeight(weight);

       if (enabled)
       {
			nsNode.setCondition(com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.NodeCondition.ENABLED);
       } else {
			nsNode.setCondition(com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.NodeCondition.DISABLED);
		}
	}


    public void populateNSNode(Node node, com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.Node nsNode)
            throws BadRequestException
    {
        translateNode(node, nsNode, true);
    }

    public void populateNSNodes(Collection<Node> nodes, List<com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.Node> nsNodes)
           throws BadRequestException
    {

    	if ((nodes != null) && (nodes.size() > 0))
        {
            logger.debug(String.format("This loadBalancer has got %d nodes", nodes.size()));
			boolean forUpdate = false;
            for (Node node : nodes)
    	    {

               com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.Node nsNode = new com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.Node();
               translateNode(node, nsNode, forUpdate);
               nsNodes.add(nsNode);
            }
    	}
    }

    public void populateNSHealthMonitor(HealthMonitor monitor, com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.HealthMonitor nsMon)
           throws BadRequestException 
    {

        String monType = monitor.getType();
        Integer monDelay = monitor.getDelay();
        Integer monTimeout = monitor.getTimeout();
        Integer monRetries = monitor.getAttemptsBeforeDeactivation();

        if (monType == null)
        {
            throw  new BadRequestException("Missing attributes [type] of healthMonitor....", new Error());
        }

        nsMon.setDelay(monDelay);
        nsMon.setTimeout(monTimeout);
        nsMon.setAttemptsBeforeDeactivation(monRetries);
        

        if (monType.equals("CONNECT"))
        { 
			nsMon.setType(com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.HealthMonitorType.CONNECT);
			return; 
        }

        if (monType.equals("HTTP"))
        { 
			nsMon.setType(com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.HealthMonitorType.HTTP);
            return;
        }
        
        if (monType.equals("HTTPS"))
        {         
			nsMon.setType(com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.HealthMonitorType.HTTPS);
            return;
        }

        throw new BadRequestException("Value for attribute [IpVersion] not valid....", new Error());                      

    }


	public com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.LoadBalancer
			getLB(LoadBalancerEndpointConfiguration config, Integer lbId, Integer accountId) 
           throws PluginException
	{
        String resourceType = "loadbalancers";
        Integer resourceId = lbId;


		String serviceUrl = config.getHost().getEndpoint();
        String resourceUrl = getLBURLStr(serviceUrl, accountId, resourceType, resourceId);

        String nsLB = performRequest("GET", resourceUrl, "");
		return (com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.LoadBalancer) getResponseObject(nsLB);
	}

	public List<com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.Node>
	getAllNodes(LoadBalancerEndpointConfiguration config, Integer lbId, Integer accountId) 
           throws PluginException
    {
        String resourceType = "loadbalancers";
        Integer resourceId = lbId;
        String childResourceType = "nodes";
		String serviceUrl = config.getHost().getEndpoint();
        String resourceUrl = getLBURLStr(serviceUrl, accountId, resourceType, resourceId, childResourceType);

        String nodesAsString = performRequest("GET", resourceUrl, "");
		com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.Nodes nsNodes = (com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.Nodes) 
																						getResponseObject(nodesAsString);
		return nsNodes.getNodes();
	
	}
    public void populateSessionPersistence(SessionPersistence sp, com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.SessionPersistence nsSP)
           throws BadRequestException
    {
		String pt = sp.getPersistenceType();
		
        if (pt.equals("HTTP_COOKIE"))
        { 
			nsSP.setPersistenceType(com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.PersistenceType.HTTP_COOKIE);
            return;
        }
        
        throw new BadRequestException("Value for attribute [PersistenceType] of SessionPersistence is not valid....", new Error());                      
	}


    public void populateConnectionThrottle(ConnectionThrottle throttle, com.citrix.cloud.netscaler.pathweaver.docs.loadbalancers.api.v1.ConnectionThrottle nsThrottle)
           throws BadRequestException 
    {
		nsThrottle.setRateInterval(throttle.getRateInterval());
		nsThrottle.setMaxRequestRate(throttle.getMaxRequestRate());
    }
}
