package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findUserByEmail(String email);

    Optional<AppUser> findByUid(String uid);

    @Query(value = "SELECT * FROM app_user WHERE id IN (SELECT app_user_id FROM company LEFT JOIN app_user_companies auc ON company.id = auc.company_id WHERE company.id = ?1)", nativeQuery = true)
    List<AppUser> getAll(Long companyId);
}
