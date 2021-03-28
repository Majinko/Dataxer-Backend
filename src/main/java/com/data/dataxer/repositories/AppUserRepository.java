package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.JoinTable;
import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findUserByEmail(String email);

    Optional<AppUser> findByUid(String uid);

    Optional<AppUser> findByIdAndUid(Long id, String uid);

    Optional<AppUser> findByUidAndDefaultCompanyId(String uid, Long companyId);

    List<AppUser> findAllByDefaultCompanyId(Long companyId);
}

