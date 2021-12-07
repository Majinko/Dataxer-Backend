package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;

public interface QAppUserRepository {
    List<AppUser> all(List<Long> companyIds);

    List<AppUser> findWhereDefaultCompanyIs(Long companyId);

    Optional<AppUser> findByUid(String uid);

    Optional<AppUser> findUserWithRolesAndPrivileges(String uid, Long companyId);

    List<AppUser> getUsersByCompany(Pageable pageable, String qString, List<Long> companyIds);
}
