package org.daylight.pathweaver.service.domain.service.impl;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.daylight.pathweaver.datamodel.CoreLoadBalancerStatus;
import org.daylight.pathweaver.datamodel.CorePersistenceType;
import org.daylight.pathweaver.datamodel.CoreProtocolType;
import org.daylight.pathweaver.service.domain.entity.ConnectionThrottle;
import org.daylight.pathweaver.service.domain.entity.LoadBalancer;
import org.daylight.pathweaver.service.domain.entity.SessionPersistence;
import org.daylight.pathweaver.service.domain.exception.*;
import org.daylight.pathweaver.service.domain.repository.ConnectionThrottleRepository;
import org.daylight.pathweaver.service.domain.repository.LoadBalancerRepository;
import org.daylight.pathweaver.service.domain.service.ConnectionThrottleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConnectionThrottleServiceImpl implements ConnectionThrottleService {
    private final Log logger = LogFactory.getLog(ConnectionThrottleServiceImpl.class);

    @Autowired
    private LoadBalancerRepository loadBalancerRepository;
    @Autowired
    private ConnectionThrottleRepository connectionThrottleRepository;

    @Override
    @Transactional(value="core_transactionManager", rollbackFor = {EntityNotFoundException.class, ImmutableEntityException.class, UnprocessableEntityException.class})
    public ConnectionThrottle update(Integer loadBalancerId, ConnectionThrottle connectionThrottle) throws PersistenceServiceException {
        LoadBalancer dbLoadBalancer = loadBalancerRepository.getById(loadBalancerId);

        String status = dbLoadBalancer.getStatus();


        if (! (status.equals("ACTIVE") || status.equals("QUEUED") || status.equals("PENDING_UPDATE"))) {
            throw new ImmutableEntityException("LoadBalancer status is not in a state that allows updates");
        }


        ConnectionThrottle dbConnectionThrottle = dbLoadBalancer.getConnectionThrottle();

        if(dbConnectionThrottle == null) {
            dbConnectionThrottle = connectionThrottle;
            dbConnectionThrottle.setLoadBalancer(dbLoadBalancer);
        }

        setPropertiesForUpdate(connectionThrottle, dbConnectionThrottle);

        loadBalancerRepository.changeStatus(dbLoadBalancer.getAccountId(), dbLoadBalancer.getId(), CoreLoadBalancerStatus.PENDING_UPDATE, false);
        dbLoadBalancer.setConnectionThrottle(dbConnectionThrottle);
        dbLoadBalancer = loadBalancerRepository.update(dbLoadBalancer);
        return dbLoadBalancer.getConnectionThrottle();
    }

    @Override
    @Transactional(value="core_transactionManager", rollbackFor = {EntityNotFoundException.class})
    public void preDelete(Integer loadBalancerId) throws EntityNotFoundException, ImmutableEntityException {

        LoadBalancer dbLoadBalancer = loadBalancerRepository.getById(loadBalancerId);

        if (dbLoadBalancer.getConnectionThrottle() == null)  {
            throw new EntityNotFoundException("Connection throttle not found");
        }

        String status = dbLoadBalancer.getStatus();


        if (! (status.equals("ACTIVE") || status.equals("QUEUED") || status.equals("PENDING_UPDATE"))) {
            throw new ImmutableEntityException("LoadBalancer status is not in a state that allows updates");
        }
    }

    @Override
    @Transactional(value="core_transactionManager", rollbackFor = {EntityNotFoundException.class})
    public void delete(Integer loadBalancerId) throws EntityNotFoundException {
        connectionThrottleRepository.delete(connectionThrottleRepository.getByLoadBalancerId(loadBalancerId));
    }

    protected void setPropertiesForUpdate(final ConnectionThrottle requestConnectionThrottle, final ConnectionThrottle dbConnectionThrottle) throws BadRequestException {
        if (requestConnectionThrottle.getMaxRequestRate() != null) {
            dbConnectionThrottle.setMaxRequestRate(requestConnectionThrottle.getMaxRequestRate());
        } else {
            throw new BadRequestException("Must provide a max request rate for the request");
        }

        if (requestConnectionThrottle.getRateInterval() != null) {
            dbConnectionThrottle.setRateInterval(requestConnectionThrottle.getRateInterval());
        } else {
            throw new BadRequestException("Must provide a rate interval for the request");
        }
    }
}
