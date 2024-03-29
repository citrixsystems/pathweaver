package org.daylight.pathweaver.service.domain.repository;

import org.daylight.pathweaver.datamodel.CoreLoadBalancerStatus;
import org.daylight.pathweaver.service.domain.entity.LoadBalancer;
import org.daylight.pathweaver.service.domain.entity.LoadBalancerJoinVip;
import org.daylight.pathweaver.service.domain.entity.SessionPersistence;
import org.daylight.pathweaver.service.domain.exception.*;

import java.util.List;
import java.util.Set;

public interface LoadBalancerRepository {
    LoadBalancer getById(Integer id) throws EntityNotFoundException;

    List<LoadBalancer> getByAccountId(Integer accountId);

    LoadBalancer getByIdAndAccountId(Integer id, Integer accountId) throws EntityNotFoundException;

    LoadBalancer create(LoadBalancer loadBalancer);

    LoadBalancer update(LoadBalancer loadBalancer);

    Integer getNumNonDeletedLoadBalancersForAccount(Integer accountId);

    void changeStatus(Integer accountId, Integer loadbalancerId, String newStatus, boolean allowConcurrentModifications) throws EntityNotFoundException, UnprocessableEntityException, ImmutableEntityException;

    void changeStatus(Integer accountId, Integer loadbalancerId, String newStatus) throws EntityNotFoundException, UnprocessableEntityException, ImmutableEntityException;

    void updatePortInJoinTable(LoadBalancer lb);

    boolean canUpdateToNewPort(Integer newPort, Set<LoadBalancerJoinVip> setToCheckAgainst);

    boolean testAndSetStatus(Integer accountId, Integer loadbalancerId, String statusToChangeTo, boolean allowConcurrentModifications) throws EntityNotFoundException, UnprocessableEntityException;

    LoadBalancer changeStatus(LoadBalancer loadBalancer, String status) throws EntityNotFoundException;

    List<LoadBalancer> getLoadBalancersWithStatus(String status);
}
