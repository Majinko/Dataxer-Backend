package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.AppUser;

import java.math.BigDecimal;

public interface QSalaryRepository {

    BigDecimal getPriceFromSalaryByUserFinishIsNull(AppUser user, Long companyId);

}
