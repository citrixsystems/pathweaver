package org.daylight.pathweaver.plugin.common.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.daylight.pathweaver.plugin.common.entity.*;
import org.daylight.pathweaver.common.ip.exception.IPStringConversionException1;
import org.daylight.pathweaver.service.domain.common.Constants;
import org.daylight.pathweaver.service.domain.entity.*;

import org.daylight.pathweaver.service.domain.exception.*;

import org.daylight.pathweaver.plugin.common.service.PluginVirtualIpService;

import org.daylight.pathweaver.plugin.common.repository.HostRepository;
import org.daylight.pathweaver.plugin.common.repository.ClusterRepository;
import org.daylight.pathweaver.plugin.common.repository.PluginVirtualIpRepository;

import org.daylight.pathweaver.service.domain.repository.VirtualIpRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: youcef
 * Date: 3/1/12
 * Time: 2:24 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class PluginVirtualIpServiceImpl implements PluginVirtualIpService {

    private final Log logger = LogFactory.getLog(PluginVirtualIpServiceImpl.class);

    @Autowired
    private PluginVirtualIpRepository pluginVirtualIpRepository;


    @Autowired
    private HostRepository hostRepository;


    @Override
    @Transactional(value="plugin_transactionManager")
    public LoadBalancer assignVipsToLoadBalancer(LoadBalancer loadBalancer) throws PersistenceServiceException, EntityNotFoundException {

        if (!loadBalancer.getLoadBalancerJoinVipSet().isEmpty()) {

            for (LoadBalancerJoinVip loadBalancerJoinVip : loadBalancer.getLoadBalancerJoinVipSet()) {
                // Add a new vip to set

                VirtualIp vip = loadBalancerJoinVip.getVirtualIp();

                LoadBalancerHost lbHost = hostRepository.getLBHost(loadBalancer.getId());

                if (lbHost == null)  {
                    throw new PersistenceServiceException(new Exception(String.format("Cannot find host of loadbalancer %d", loadBalancer.getId())));
                }

                Host host = lbHost.getHost();

                Cluster cluster = host.getCluster();

                String address;

                if (vip.getIpVersion() == IpVersion.IPV4) {
                    address = allocateIpv4VirtualIp(vip, loadBalancer.getAccountId(), cluster);
                } else {
                    address = allocateIpv6VirtualIp(vip, loadBalancer.getAccountId(), cluster);
                }

                vip.setAddress(address);

            }
        }

        return loadBalancer;
    }

    @Transactional(value="plugin_transactionManager")
    public String allocateIpv4VirtualIp(VirtualIp virtualIp, Integer accountId, Cluster cluster) throws OutOfVipsException, EntityNotFoundException {
        Calendar timeConstraintForVipReuse = Calendar.getInstance();
        timeConstraintForVipReuse.add(Calendar.DATE, -Constants.NUM_DAYS_BEFORE_VIP_REUSE);

        if (virtualIp.getVipType() == null) {
            virtualIp.setVipType(VirtualIpType.PUBLIC);
        }

        try {
            return pluginVirtualIpRepository.allocateIpv4VipBeforeDate(virtualIp, cluster, timeConstraintForVipReuse);
        } catch (OutOfVipsException e) {
            logger.warn(String.format("Out of IPv4 virtual ips that were de-allocated before '%s'.", timeConstraintForVipReuse.getTime()));
            return pluginVirtualIpRepository.allocateIpv4VipAfterDate(virtualIp, cluster, timeConstraintForVipReuse);
        }
    }

    @Transactional(value="plugin_transactionManager")
    public String allocateIpv6VirtualIp(VirtualIp vip, Integer accountId, Cluster c) throws EntityNotFoundException {
         return pluginVirtualIpRepository.allocateIpv6Vip(vip, accountId, c);
    }

    @Transactional(value="plugin_transactionManager")
    public void removeAllVipsFromLoadBalancer(LoadBalancer loadBalancer) {

        if (!loadBalancer.getLoadBalancerJoinVipSet().isEmpty()) {

            for (LoadBalancerJoinVip loadBalancerJoinVip : loadBalancer.getLoadBalancerJoinVipSet()) {

                VirtualIp vip = loadBalancerJoinVip.getVirtualIp();
                deallocateVirtualIp(vip);
            }
        }
    }

    @Transactional(value="plugin_transactionManager")
    public void undoAllVipsFromLoadBalancer(LoadBalancer loadBalancer) {

        if (!loadBalancer.getLoadBalancerJoinVipSet().isEmpty()) {

            for (LoadBalancerJoinVip loadBalancerJoinVip : loadBalancer.getLoadBalancerJoinVipSet()) {

                VirtualIp vip = loadBalancerJoinVip.getVirtualIp();
                resetVirtualIp(vip);
            }
        }
    }

    private void deallocateVirtualIp(VirtualIp vip)
    {
        pluginVirtualIpRepository.deallocateVirtualIp(vip);
        vip.setAddress(null);
    }

    private void resetVirtualIp(VirtualIp vip)
    {
        pluginVirtualIpRepository.resetVirtualIp(vip);
        vip.setAddress(null);
    }


    @Override
    @Transactional(value="plugin_transactionManager")
    public final VirtualIpv4 createVirtualIpCluster(VirtualIpv4 vipCluster) throws PersistenceServiceException {

        VirtualIpv4 dbVipCluster = pluginVirtualIpRepository.createVirtualIpv4(vipCluster);

        return dbVipCluster;
    }

    @Override
    @Transactional(value="plugin_transactionManager")
    public final VirtualIpv4 getVirtualIpCluster(Integer vipId) {

        VirtualIpv4 dbVipCluster = pluginVirtualIpRepository.getVirtualIpv4(vipId);

        return dbVipCluster;
    }


}
