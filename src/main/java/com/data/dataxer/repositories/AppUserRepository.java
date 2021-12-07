package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.AppUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findUserByEmail(String email);

    Optional<AppUser> findByUid(String uid);

    Optional<AppUser> findByIdAndUid(Long id, String uid);

    Optional<AppUser> findByUidAndDefaultCompanyId(String uid, Long companyId);

    @Query("SELECT u FROM AppUser u left join fetch u.roles where u.uid = ?1 and u.defaultCompany.id = ?2")
    AppUser findByUidAndDefaultCompanyIdWithRoles(String uid, Long companyId);

    List<AppUser> findAllByDefaultCompanyId(Long companyId);

    @Query("SELECT u FROM AppUser u WHERE u.id in (SELECT c.appUsers FROM Company c )")
    List<AppUser> findAllByDefaultCompanyIdOrderByIdAsc(Pageable pageable, Long companyId);

    Long countAllByDefaultCompanyId(Long companyId);
}

