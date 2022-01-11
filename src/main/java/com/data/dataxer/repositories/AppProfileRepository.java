package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.AppProfile;
import com.data.dataxer.models.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AppProfileRepository extends JpaRepository<AppProfile, Long> {
    Optional<AppProfile> findAllByTitle(String title);

    List<AppProfile> findAllByAppUsersIn(List<AppUser> appUsers);

    @Query("SELECT ap FROM AppProfile ap left join fetch ap.appUsers where ap.id = ?1")
    AppProfile findByIdWithUsers(Long id);
}
