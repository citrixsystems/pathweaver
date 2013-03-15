package org.daylight.pathweaver.service.domain.service;

import org.daylight.pathweaver.service.domain.entity.AccountLimit;
import org.daylight.pathweaver.service.domain.entity.AccountLimitType;
import org.daylight.pathweaver.service.domain.exception.EntityNotFoundException;
import org.daylight.pathweaver.service.domain.exception.LimitReachedException;
import org.daylight.pathweaver.service.domain.exception.PersistenceServiceException;

public interface AccountLimitService {

    AccountLimit create(Integer accountId, AccountLimit accountLimit) throws PersistenceServiceException;

    void verifyLoadBalancerLimit(Integer accountId) throws EntityNotFoundException, LimitReachedException;

    int getLimit(Integer accountId, AccountLimitType accountLimitType) throws EntityNotFoundException;

    void delete(Integer accountId) throws PersistenceServiceException;
}
