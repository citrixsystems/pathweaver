package org.daylight.pathweaver.service.domain.event.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.daylight.pathweaver.service.domain.event.entity.Alert;
import org.daylight.pathweaver.service.domain.event.entity.AlertStatus;
import org.daylight.pathweaver.service.domain.exception.BadRequestException;
import org.daylight.pathweaver.service.domain.exception.EntityNotFoundException;
import org.daylight.pathweaver.service.domain.pojo.CustomQuery;
import org.daylight.pathweaver.service.domain.pojo.QueryParameter;
import org.daylight.pathweaver.common.converters.exceptions.ConverterException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.daylight.pathweaver.common.converters.DateTimeConverters.isoTocal;
import static org.daylight.pathweaver.common.converters.PrimitiveConverters.integerList2cdString;

@Repository
@Transactional(value="core_transactionManager")
public class AlertRepository {

    private final Log logger = LogFactory.getLog(AlertRepository.class);

    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";
    private static final String INVALID_START_DATE = "Invalid startDate";
    private static final String INVALID_END_DATE = "Invalid endDate";

    private static final Integer MAX_RESULTS_LIMIT = 10;
    private static final Integer MAX_LIMIT_VALUE = 100;
    private static final Integer DAYS_KEEP_ALERTS = 90;

    @PersistenceContext(unitName = "loadbalancing")
    private EntityManager entityManager;

    public AlertRepository() {
    }

    public AlertRepository(EntityManager em) {
        this.entityManager = em;
    }

    public void save(Alert alert) {
        Calendar now = Calendar.getInstance();
        alert.setCreated(now);
        entityManager.persist(alert);
    }

    public Alert getById(Integer id) throws EntityNotFoundException {
        Alert cl = entityManager.find(Alert.class, id);
        if (cl == null) {
            String errMsg = String.format("Cannot access alert {id=%d}", id);
            logger.warn(errMsg);
            throw new EntityNotFoundException(errMsg);
        }
        return cl;
    }

    public List<Alert> getByLoadBalancersByIds(List<Integer> loadBalancerIds, String startDate, String endDate) throws BadRequestException {
        CustomQuery cq;
        String intsAsString;
        String qStr;
        String qformat;
        Calendar startCal;
        Calendar endCal;
        Query q;
        try {
            intsAsString = integerList2cdString(loadBalancerIds);
        } catch (ConverterException ex) {
            Logger.getLogger(AlertRepository.class.getName()).log(Level.SEVERE, null, ex);
            throw new BadRequestException("loadBalancerIds can not be Null", ex);
        }
        qformat = "SELECT al From Alert al where al.loadbalancerId "
                + "IN (%s)";

        qStr = String.format(qformat, intsAsString);
        cq = new CustomQuery(qStr);
        cq.setWherePrefix("");
        if (startDate != null) {
            try {
                startCal = isoTocal(startDate);
            } catch (ConverterException ex) {
                Logger.getLogger(AlertRepository.class.getName()).log(Level.SEVERE, null, ex);
                throw new BadRequestException(INVALID_START_DATE, ex);
            }
            cq.addParam("al.created", ">=", START_DATE, startCal);
        }

        if (endDate != null) {
            try {
                endCal = isoTocal(endDate);
            } catch (ConverterException ex) {
                Logger.getLogger(AlertRepository.class.getName()).log(Level.SEVERE, null, ex);
                throw new BadRequestException(INVALID_END_DATE, ex);
            }
            cq.addParam("al.created", "<=", END_DATE, endCal);
        }
        if (cq.getQueryParameters().size() > 0) {
            cq.setWherePrefix(" and ");
        }
        q = entityManager.createQuery(cq.getQueryString());
        for (QueryParameter qp : cq.getQueryParameters()) {
            String pname = qp.getPname();
            Object val = qp.getValue();
            q.setParameter(pname, val);
        }
        return q.getResultList();
    }

    public List<Alert> getByAccountId(Integer accountId, String startDate, String endDate) throws BadRequestException {
        CustomQuery cq;
        Calendar startCal;
        Calendar endCal;
        Query q;


        cq = new CustomQuery("from Alert a");
        cq.addParam("a.accountId", "=", "aid", accountId);
        if (startDate != null) {
            try {
                startCal = isoTocal(startDate);
            } catch (ConverterException ex) {
                Logger.getLogger(AlertRepository.class.getName()).log(Level.SEVERE, null, ex);
                throw new BadRequestException(INVALID_START_DATE, ex);
            }
            cq.addParam("a.created", ">=", START_DATE, startCal);
        }

        if (endDate != null) {
            try {
                endCal = isoTocal(endDate);
            } catch (ConverterException ex) {
                Logger.getLogger(AlertRepository.class.getName()).log(Level.SEVERE, null, ex);
                throw new BadRequestException(INVALID_END_DATE, ex);
            }
            cq.addParam("a.created", "<=", END_DATE, endCal);
        }

        q = entityManager.createQuery(cq.getQueryString());
        for (QueryParameter qp : cq.getQueryParameters()) {
            String pname = qp.getPname();
            Object val = qp.getValue();
            q.setParameter(pname, val);
        }

        for (QueryParameter qp : cq.getUnquotedParameters()) {
            String pname = qp.getPname();
            Object val = qp.getValue();
            q.setParameter(pname, val);
        }
        return q.getResultList();
    }

    public List<Alert> getByClusterId(Integer clusterId, String startDate, String endDate) throws BadRequestException {
        CustomQuery cq;
        String qStr;
        Calendar startCal;
        Calendar endCal;
        Query q;

        qStr = "from Alert a where a.loadbalancerId in ";
        qStr += "(SELECT h.id from Host h where h.cluster.id = :cid)";

        cq = new CustomQuery(qStr);
        cq.addUnquotedParam("cid", clusterId);
        cq.setWherePrefix("");
        if (startDate != null) {
            try {
                startCal = isoTocal(startDate);
            } catch (ConverterException ex) {
                Logger.getLogger(AlertRepository.class.getName()).log(Level.SEVERE, null, ex);
                throw new BadRequestException(INVALID_START_DATE, ex);
            }
            cq.addParam("a.created", ">=", START_DATE, startCal);
        }

        if (endDate != null) {
            try {
                endCal = isoTocal(endDate);
            } catch (ConverterException ex) {
                Logger.getLogger(AlertRepository.class.getName()).log(Level.SEVERE, null, ex);
                throw new BadRequestException(INVALID_END_DATE, ex);
            }
            cq.addParam("a.created", "<=", END_DATE, endCal);
        }
        if (cq.getQueryParameters().size() > 0) {
            cq.setWherePrefix(" and ");
        }
        q = entityManager.createQuery(cq.getQueryString());
        for (QueryParameter qp : cq.getQueryParameters()) {
            String pname = qp.getPname();
            Object val = qp.getValue();
            q.setParameter(pname, val);
        }

        for (QueryParameter qp : cq.getUnquotedParameters()) {
            String pname = qp.getPname();
            Object val = qp.getValue();
            q.setParameter(pname, val);
        }
        return q.getResultList();
    }

    public Alert update(Alert alert) {
        Alert resultAlert;

        logger.info("Updating Alert " + alert.getId() + "...");
        resultAlert = entityManager.merge(alert);
        entityManager.flush();
        return resultAlert;
    }

    public List<Alert> getForAccount() {
        return entityManager.createQuery("SELECT ht FROM Alert ht where ht.accountId is not null and ht.loadbalancerId is not null").getResultList();
    }

    public List<Alert> getForLoadBalancer(Integer loadbalancerId) {
        String queryStr = "SELECT ht FROM Alert ht where  ht.loadbalancerId = :loadbalancerId";
        return entityManager.createQuery(queryStr).setParameter("loadbalancerId", loadbalancerId).getResultList();
    }

    public List<Alert> getByAccountId(Integer marker, Integer limit, Integer accountId, String startDate, String endDate) throws BadRequestException {
        Calendar endCal, startCal;

        if (marker == null) {
            marker = 0;
        }

        if (limit == null) {
            limit = MAX_RESULTS_LIMIT;
        }

        if (endDate == null) {
            endCal = Calendar.getInstance();
        } else {
            try {
                endCal = isoTocal(endDate);
            } catch (ConverterException ex) {
                Logger.getLogger(AlertRepository.class.getName()).log(Level.SEVERE, null, ex);
                throw new BadRequestException(INVALID_END_DATE, ex);
            }
        }

        if (startDate == null) {
            startCal = new GregorianCalendar(2000, Calendar.OCTOBER, 10);
        } else {
            try {
                startCal = isoTocal(startDate);
            } catch (ConverterException ex) {
                Logger.getLogger(AlertRepository.class.getName()).log(Level.SEVERE, null, ex);
                throw new BadRequestException(INVALID_START_DATE, ex);
            }
        }

        String queryStr = "SELECT ht FROM Alert ht WHERE ht.accountId = :accountId AND ht.created <= :endDate AND ht.created >= :startDate";

        List<Alert> results = entityManager.createQuery(queryStr).setFirstResult(marker).setMaxResults(limit).setParameter("accountId", accountId).setParameter(START_DATE, startCal).setParameter(END_DATE, endCal).getResultList();
        
        return results;
    }

    public List<Alert> getAll(String status, Integer... p) {
        List<Alert> alts = new ArrayList<Alert>();

        AlertStatus lbStatus = null;
        if (status != null) {
            try {
                lbStatus = AlertStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                lbStatus = null;
            }
        }

        Query query;
        query = entityManager.createQuery("SELECT h FROM Alert h");

        if (lbStatus != null) {
            query = entityManager.createQuery("SELECT h FROM Alert h WHERE h.status = :status").setParameter("status", lbStatus);
        }

        if (p.length >= 2) {
            Integer marker = p[0];
            Integer limit = p[1];
            if (limit == null || limit > MAX_LIMIT_VALUE) {
                limit = MAX_LIMIT_VALUE;
            }
            if (marker == null) {
                marker = 0;
            }
            query = query.setFirstResult(marker).setMaxResults(limit);
        }
        alts = query.getResultList();
        return alts;
    }

    public List<Alert> getAllUnacknowledged(Integer... p) {
        List<Alert> alts = new ArrayList<Alert>();
        Query query = entityManager.createQuery("SELECT h FROM Alert h where h.status = 'UNACKNOWLEDGED'");
        if (p.length >= 2) {
            Integer marker = p[0];
            Integer limit = p[1];
            if (limit == null || limit > MAX_LIMIT_VALUE) {
                limit = MAX_LIMIT_VALUE;
            }
            if (marker == null) {
                marker = 0;
            }
            query = query.setFirstResult(marker).setMaxResults(limit);
        }
        alts = query.getResultList();
        return alts;
    }

    public void removeAlertEntries() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -DAYS_KEEP_ALERTS);
        entityManager.createQuery("DELETE FROM Alert a where a.created <= :days and a.status ='ACKNOWLEDGED'").setParameter("days", cal).executeUpdate();
    }
}
