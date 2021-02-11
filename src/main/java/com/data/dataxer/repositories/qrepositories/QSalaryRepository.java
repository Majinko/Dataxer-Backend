package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.Salary;

import java.math.BigDecimal;
import java.util.List;

public interface QSalaryRepository {

    BigDecimal getPriceFromSalaryByUserFinishIsNull(AppUser user, Long companyId);

    List<Salary> getSalariesForUsersByIds(List<Long> userIds, Long companyId);
}
