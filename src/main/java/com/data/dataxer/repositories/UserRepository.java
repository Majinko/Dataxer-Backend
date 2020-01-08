package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.DataxerUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<DataxerUser, Long> {
    Optional<DataxerUser> findUserByEmail(String email);

    Optional<DataxerUser> findByUid(String uid);
}
