package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findUserByEmail(String email);

    Optional<AppUser> findByUid(String uid);
}
