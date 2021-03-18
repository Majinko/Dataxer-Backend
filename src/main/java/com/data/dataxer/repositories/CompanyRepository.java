package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findAllByAppUsersIn(List<AppUser> appUsers);

    Optional<Company> findByIdAndAppUsersIn(Long companyId, List<AppUser> appUsers);

    @Query("SELECT c FROM Company c left join fetch c.appUsers where c.id = ?1")
    Company findByIdWithUsers(Long id);
}
