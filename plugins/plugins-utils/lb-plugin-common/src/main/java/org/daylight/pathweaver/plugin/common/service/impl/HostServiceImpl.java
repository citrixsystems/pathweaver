package org.daylight.pathweaver.plugin.common.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.daylight.pathweaver.plugin.common.entity.Host;
import org.daylight.pathweaver.plugin.common.entity.LoadBalancerHost;
import org.daylight.pathweaver.service.domain.exception.PersistenceServiceException;
import org.daylight.pathweaver.plugin.common.repository.HostRepository;
import org.daylight.pathweaver.plugin.common.service.HostService;
import org.daylight.pathweaver.service.domain.service.impl.HealthMonitorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HostServiceImpl implements HostService {
    private final Log logger = LogFactory.getLog(HostServiceImpl.class);

    @Autowired
    private HostRepository hostRepository;


    @Override
    @Transactional(value="plugin_transactionManager")
    public final LoadBalancerHost createLoadBalancerHost(LoadBalancerHost lbHost) throws PersistenceServiceException {

        try {
            return hostRepository.createLoadBalancerHost(lbHost);
        } catch (Exception e) {
            throw new PersistenceServiceException(e);
        }

    }

    @Override
    @Transactional(value="plugin_transactionManager")
    public final void removeLoadBalancerHost(LoadBalancerHost lbHost) throws PersistenceServiceException {

        try {
            hostRepository.removeLoadBalancerHost(lbHost);
        } catch (Exception e) {
            throw new PersistenceServiceException(e);
        }

    }

    @Override
    @Transactional(value="plugin_transactionManager")
    public final LoadBalancerHost getLoadBalancerHost(Integer loadBalancerId) {
        return hostRepository.getLBHost(loadBalancerId);
    }

    @Override
    public Host getDefaultActiveHost() {

        List<Host> hosts = hostRepository.getHosts();



        if (hosts == null || hosts.size() <= 0) {
            return null;
        }
        if (hosts.size() == 1) {
            Host host = hosts.get(0);
            return (host);
        } else {
            Host host =  hostRepository.getHostWithMinimumLoadBalancers(hosts);
            return (host);
        }
    }
}
