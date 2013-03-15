package org.daylight.pathweaver.plugin.netscaler;


import java.io.IOException;

import java.net.URL;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.jersey.api.client.*;
import javax.ws.rs.core.Response.Status;

public class NSRequest
{
    private static Log logger = LogFactory.getLog(NetScalerPluginImpl.class.getName());



    static String perform_request(String method, String urlStr, Map<String,String> headers, String body)
    throws IOException
    {
    	
    	ClientResponse response;

        logger.debug("[issuing request: " + method + " " + urlStr + "]");

        Client client = Client.create();
        WebResource  resource = client.resource(urlStr);

        WebResource.Builder resourceBuilder = resource.getRequestBuilder();
        

        // write  headers
        for (Map.Entry<String,String> header : headers.entrySet())
        {
            logger.debug(header.getKey() +  ":" + header.getValue());

            resourceBuilder.header(header.getKey(), header.getValue());
        }


        response = null;
        
        if (method.toUpperCase().equals("GET"))
        {
        	response = resourceBuilder.get(ClientResponse.class);
        }
        
        if (method.toUpperCase().equals("POST"))
        {
        	response = resourceBuilder.post(ClientResponse.class, body);
        }
        
        
        if (method.toUpperCase().equals("PUT"))
        {
        	response = resourceBuilder.put(ClientResponse.class, body);
        }
        
        if (method.toUpperCase().equals("DELETE"))
        {
        	response = resourceBuilder.delete(ClientResponse.class);
        }
        
        String resp_body;
        
        if (response != null)
        {
        	int statuscode = response.getStatus();
        	

        	logger.debug("Status code of response is: " + statuscode);
        
        	resp_body = response.getEntity(String.class);
        	logger.debug("Response body: " + resp_body);
        	
        	if (!method.toUpperCase().equals("GET") && statuscode != Status.ACCEPTED.getStatusCode())
        	{
				if(statuscode != Status.CREATED.getStatusCode() && statuscode != Status.OK.getStatusCode()) {
					throw new IOException("Error : " + resp_body);
                }
        	}
        	
        } else {
        	logger.debug("response was set to null");
        	resp_body = null;
        }
        
        return resp_body;
    }
}
