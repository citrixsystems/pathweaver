package org.daylight.pathweaver.service.domain.service;

import org.daylight.pathweaver.service.domain.entity.LoadBalancer;
import org.daylight.pathweaver.service.domain.entity.Node;
import org.daylight.pathweaver.service.domain.exception.BadRequestException;
import org.daylight.pathweaver.service.domain.exception.EntityNotFoundException;
import org.daylight.pathweaver.service.domain.exception.ImmutableEntityException;
import org.daylight.pathweaver.service.domain.exception.UnprocessableEntityException;

import java.util.List;
import java.util.Set;

public interface NodeService {

    Set<Node> createNodes(LoadBalancer loadBalancer) throws EntityNotFoundException, ImmutableEntityException, UnprocessableEntityException, BadRequestException;

    LoadBalancer updateNode(LoadBalancer loadBalancer) throws EntityNotFoundException, ImmutableEntityException, UnprocessableEntityException;

    List<String> prepareForNodesDeletion(Integer accountId,Integer loadBalancerId,List<Integer> ids) throws EntityNotFoundException, UnprocessableEntityException, ImmutableEntityException;

    boolean detectDuplicateNodes(LoadBalancer dbLoadBalancer, LoadBalancer queueLb);

    boolean areAddressesValidForUse(Set<Node> nodes, LoadBalancer dbLb);
}
