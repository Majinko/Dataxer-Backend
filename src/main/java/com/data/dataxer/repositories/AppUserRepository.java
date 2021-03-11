package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findUserByEmail(String email);

    Optional<AppUser> findByUid(String uid);

    Optional<AppUser> findByIdAndUid(Long id, String uid);

    Optional<AppUser> findByUidAndCompaniesIn(String uid, List<Company> companies);

    List<AppUser> findAllByCompaniesIn(List<Company> companies);
}

