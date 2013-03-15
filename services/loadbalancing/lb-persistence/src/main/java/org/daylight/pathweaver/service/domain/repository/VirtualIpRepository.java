package org.daylight.pathweaver.service.domain.repository;

import org.daylight.pathweaver.service.domain.entity.*;
import org.daylight.pathweaver.service.domain.exception.EntityNotFoundException;
import org.daylight.pathweaver.service.domain.exception.OutOfVipsException;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public interface VirtualIpRepository {

    void persist(Object obj);

    VirtualIp create(VirtualIp vip);

    void update(VirtualIp vip);

    List<LoadBalancerJoinVip> getJoinRecordsForVip(VirtualIp virtualIp);

    List<VirtualIp> getVipsByAccountId(Integer accountId);

    List<VirtualIp> getVipsByLoadBalancerId(Integer loadBalancerId);

    void removeJoinRecord(LoadBalancerJoinVip loadBalancerJoinVip);

    Account getLockedAccountRecord(Integer accountId);
    
    Map<Integer, List<LoadBalancer>> getPorts(Integer vid);

    VirtualIp getById(Integer id) throws EntityNotFoundException;

    void removeVirtualIp(VirtualIp vip);

    List<Integer> getAccountIdsAlreadyShaHashed();

}