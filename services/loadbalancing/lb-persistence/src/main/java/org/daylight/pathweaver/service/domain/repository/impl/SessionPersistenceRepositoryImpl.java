package org.daylight.pathweaver.service.domain.repository.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.daylight.pathweaver.service.domain.entity.*;
import org.daylight.pathweaver.service.domain.exception.EntityNotFoundException;
import org.daylight.pathweaver.service.domain.repository.SessionPersistenceRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Repository
@Transactional(value="core_transactionManager")
public class SessionPersistenceRepositoryImpl implements SessionPersistenceRepository {
    private final Log logger = LogFactory.getLog(SessionPersistenceRepositoryImpl.class);
    private static final String entityNotFound = "Session persistence not found";
    @PersistenceContext(unitName = "loadbalancing")
    private EntityManager entityManager;

    @Override
    public SessionPersistence getByLoadBalancerId(Integer loadBalancerId) throws EntityNotFoundException {
        LoadBalancer loadBalancer = new LoadBalancer();
        loadBalancer.setId(loadBalancerId);

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<SessionPersistence> criteria = builder.createQuery(SessionPersistence.class);
        Root<SessionPersistence> persistenceRoot = criteria.from(SessionPersistence.class);
        Predicate belongsToLoadBalancer = builder.equal(persistenceRoot.get(SessionPersistence_.loadBalancer), loadBalancer);

        criteria.select(persistenceRoot);
        criteria.where(belongsToLoadBalancer);

        try {
            return entityManager.createQuery(criteria).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            throw new EntityNotFoundException(entityNotFound, e);
        } catch (NonUniqueResultException e) {
            logger.error("More than one session persistence detected!", e);
            throw new EntityNotFoundException(entityNotFound, e);
        }
    }

    @Override
    public void delete(SessionPersistence sessionPersistence) throws EntityNotFoundException {
        if (sessionPersistence == null) {
            throw new EntityNotFoundException(entityNotFound);
        }
        sessionPersistence = entityManager.merge(sessionPersistence); // Re-attach hibernate instance
        entityManager.remove(sessionPersistence);
    }
}
