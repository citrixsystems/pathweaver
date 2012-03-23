package org.openstack.atlas.service.domain.repository;

import org.openstack.atlas.service.domain.entity.*;
import org.openstack.atlas.service.domain.exception.OutOfVipsException;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public interface VirtualIpRepository {

    void persist(Object obj);

    List<LoadBalancerJoinVip> getJoinRecordsForVip(VirtualIp virtualIp);

    List<VirtualIp> getVipsByAccountId(Integer accountId);

    List<VirtualIp> getVipsByLoadBalancerId(Integer loadBalancerId);

    void removeJoinRecord(LoadBalancerJoinVip loadBalancerJoinVip);

    void deallocateVirtualIp(VirtualIp virtualIp);

    VirtualIp allocateIpv4VipBeforeDate(Calendar vipReuseTime, VirtualIpType vipType) throws OutOfVipsException;

    VirtualIp allocateIpv4VipAfterDate(Calendar vipReuseTime, VirtualIpType vipType) throws OutOfVipsException;

    Account getLockedAccountRecord(Integer accountId);
    
    Map<Integer, List<LoadBalancer>> getPorts(Integer vid);
}
