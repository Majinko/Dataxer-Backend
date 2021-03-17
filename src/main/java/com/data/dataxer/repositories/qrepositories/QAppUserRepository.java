package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.AppUser;

import java.util.List;
import java.util.Optional;

public interface QAppUserRepository {
    List<AppUser> all(List<Long> companyIds);

    List<AppUser> findWhereDefaultCompanyIs(Long companyId);

    Optional<AppUser> findByUid(String uid);
}
