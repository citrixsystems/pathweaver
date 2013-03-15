package org.daylight.pathweaver.service.domain.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.daylight.pathweaver.common.converters.StringConverter;
import org.daylight.pathweaver.service.domain.common.Constants;
import org.daylight.pathweaver.service.domain.entity.*;
import org.daylight.pathweaver.service.domain.exception.DeletedStatusException;
import org.daylight.pathweaver.service.domain.exception.EntityNotFoundException;
import org.daylight.pathweaver.service.domain.pojo.NodeMap;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

public interface NodeRepository {
    Set<Node> addNodes(LoadBalancer loadBalancer, Collection<Node> nodes);

    LoadBalancer update(LoadBalancer loadBalancer);

    LoadBalancer deleteNodes(LoadBalancer lb, Set<Integer> nodeIds);

    Node getNodeById(Integer loadBalancerId, Integer accountId, Integer nodeId) throws EntityNotFoundException;

    Node getNodesByLoadBalancer(LoadBalancer loadBalancer, Integer nid) throws EntityNotFoundException, DeletedStatusException;

    List<Node> getNodesByIds(Collection<Integer> ids);

    Set<Node> getNodesByAccountIdLoadBalancerId(Integer loadBalancerId, Integer accountId) throws EntityNotFoundException;

    NodeMap getNodeMap(Integer accountId, Integer loadbalancerId) throws EntityNotFoundException;
}
