package org.daylight.pathweaver.service.domain.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.daylight.pathweaver.service.domain.entity.BlacklistItem;
import org.daylight.pathweaver.service.domain.exception.EntityNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional(value="core_transactionManager")
public class BlacklistRepository {
    private final Log logger = LogFactory.getLog(BlacklistRepository.class);

    @PersistenceContext(unitName = "loadbalancing")
    private EntityManager entityManager;

    public List<BlacklistItem> getAllBlacklistItems() {
        Query query = entityManager.createQuery("SELECT b FROM BlacklistItem b");
        return query.getResultList();
    }

}
