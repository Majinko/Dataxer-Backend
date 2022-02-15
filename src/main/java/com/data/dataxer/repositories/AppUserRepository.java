package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.AppProfile;
import com.data.dataxer.models.domain.AppUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findUserByEmail(String email);

    Optional<AppUser> findByIdAndUid(Long id, String uid);

    Optional<AppUser> findByUidAndDefaultProfileId(String uid, Long appProfileId);

    @Query("SELECT u FROM AppUser u left join fetch u.roles where u.uid = ?1")
    AppUser findByUid(String uid); // todo add company

    List<AppUser> findAllByDefaultProfileId(Long appProfileId);

    @Query("SELECT u FROM AppUser u WHERE u.id in (SELECT c.appUsers FROM Company c )")
    List<AppUser> findAllByDefaultProfileIdOrderByIdAsc(Pageable pageable, Long appProfileId);

    Long countAllByDefaultProfileId(Long appProfileId);
}

