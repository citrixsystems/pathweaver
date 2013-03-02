
/**
 * Created by IntelliJ IDEA.
 * User: youcef
 * Date: 2/29/12
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */

package org.daylight.pathweaver.adapter.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.daylight.pathweaver.adapter.common.config.LoadBalancerEndpointConfiguration;
import org.daylight.pathweaver.adapter.common.config.PublicApiServiceConfigurationKeys;
import org.daylight.pathweaver.adapter.common.entity.LoadBalancerHost;
import org.daylight.pathweaver.adapter.common.repository.HostRepository;
import org.daylight.pathweaver.adapter.exception.AdapterException;
import org.daylight.pathweaver.adapter.common.entity.Cluster;
import org.daylight.pathweaver.adapter.common.entity.Host;
import org.daylight.pathweaver.adapter.common.service.HostService;
import org.daylight.pathweaver.common.config.Configuration;
import org.daylight.pathweaver.common.crypto.CryptoUtil;
import org.daylight.pathweaver.common.crypto.exception.DecryptException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EndpointUtils {

    private static Log logger = LogFactory.getLog(EndpointUtils.class.getName());


    @Autowired
    private Configuration configuration;

    @Autowired
    private HostService hostService;

    @Autowired
    private HostRepository hostRepository;


    private static EndpointUtils endpointUtils;


    private static void setEndpointUtilsSingleton() {

        if (endpointUtils == null) {
            endpointUtils = new EndpointUtils();
        }
    }


    public static LoadBalancerEndpointConfiguration getConfigbyLoadBalancerId(Integer lbId)  {
        setEndpointUtilsSingleton();
        return endpointUtils.getConfigbyLoadBalancerIdInternal(lbId);

    }

    private LoadBalancerEndpointConfiguration getConfigbyLoadBalancerIdInternal(Integer lbId) {

        if (hostService == null) {
            logger.debug("hostService is null !");
        }

        LoadBalancerHost lbHost = hostService.getLoadBalancerHost(lbId);
        Host host = lbHost.getHost();
        return getConfigbyHost(host);
    }


    public static LoadBalancerEndpointConfiguration getConfigbyHost(Host host) {
        setEndpointUtilsSingleton();
        return endpointUtils.getConfigbyHostInternal(host);
    }

    private LoadBalancerEndpointConfiguration getConfigbyHostInternal(Host host) {
        try {
            Cluster cluster = host.getCluster();
            Host endpointHost = hostRepository.getEndPointHost(cluster.getId());
            List<String> failoverHosts = hostRepository.getFailoverHostNames(cluster.getId());
            String logFileLocation = configuration.getString(PublicApiServiceConfigurationKeys.access_log_file_location);
            return new LoadBalancerEndpointConfiguration(endpointHost, cluster.getUsername(), CryptoUtil.decrypt(cluster.getPassword()), host, failoverHosts, logFileLocation);
        } catch(DecryptException except)
        {
            logger.error(String.format("Decryption exception: ", except.getMessage()));
            return null;
        }
    }



    private static boolean isConnectionExcept(Exception exc) {
        String faultString = exc.getMessage();
        if (faultString == null) {
            return false;
        }
        if (faultString.split(":")[0].equals("java.net.ConnectException")) {
            return true;
        }
        return false;
    }


    private void checkAndSetIfEndPointBadInternal(LoadBalancerEndpointConfiguration config, Exception exc) throws AdapterException, Exception {
        Host badHost = config.getHost();
        if (isConnectionExcept(exc)) {
            logger.error(String.format("Endpoint %s went bad marking host[%d] as bad.", badHost.getEndpoint(), badHost.getId()));
            badHost.setEndpointActive(Boolean.FALSE);
            hostRepository.update(badHost);
        }
    }

    protected static void checkAndSetIfEndPointBad(LoadBalancerEndpointConfiguration config, Exception exc) throws AdapterException, Exception {
        setEndpointUtilsSingleton();
        endpointUtils.checkAndSetIfEndPointBadInternal(config, exc);
    }

}
