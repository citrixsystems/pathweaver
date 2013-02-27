package org.daylight.pathweaver.service.domain.repository;

import org.daylight.pathweaver.service.domain.entity.AccountLimit;
import org.daylight.pathweaver.service.domain.entity.AccountLimitType;
import org.daylight.pathweaver.service.domain.entity.LimitType;
import org.daylight.pathweaver.service.domain.exception.EntityNotFoundException;

import java.util.List;

public interface AccountLimitRepository {

    AccountLimit create(AccountLimit accountLimit);

    List<AccountLimit> getAccountLimits(Integer accountId);

    int getLimit(Integer accountId, AccountLimitType accountLimitType) throws EntityNotFoundException;

    LimitType getLimitType(AccountLimitType accountLimitType) throws EntityNotFoundException;

    List<AccountLimit> getCustomLimitsByAccountId(Integer accountId) throws EntityNotFoundException;

    void delete(AccountLimit accountLimit) throws EntityNotFoundException;
}
