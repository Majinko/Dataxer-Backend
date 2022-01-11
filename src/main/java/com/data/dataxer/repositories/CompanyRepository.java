package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    @Query("SELECT c from Company c where c.id = ?1 and c.appProfile.id = ?2")
    Optional<Company> findByAppProfileIdAndId(Long id, Long appProfileId);

    List<Company> findAllByAppUsersIn(List<AppUser> appUsers);

    Optional<Company> findByIdAndAppUsersIn(Long companyId, List<AppUser> appUsers);

    Optional<Company> findByCin(String cin);

    @Query("SELECT c FROM Company c left join fetch c.appUsers where c.id = ?1")
    Company findByIdWithUsers(Long id);

    Company getById(Long id);
}
