package org.daylight.pathweaver.plugin.common.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.daylight.pathweaver.plugin.common.entity.Cluster;
import org.daylight.pathweaver.service.domain.exception.EntityNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Repository
@Transactional(value="plugin_transactionManager")
public class ClusterRepository {

    private final Log logger = LogFactory.getLog(ClusterRepository.class);

    @PersistenceContext(unitName = "loadbalancingplugin")
    private EntityManager entityManager;

    public Cluster getById(Integer id) throws EntityNotFoundException {
        Cluster cl = entityManager.find(Cluster.class, id);
        if (cl == null) {
            String errMsg = String.format("Cannot access cluster {id=%d}", id);
            logger.warn(errMsg);
            throw new EntityNotFoundException(errMsg);
        }
        return cl;
    }
}
