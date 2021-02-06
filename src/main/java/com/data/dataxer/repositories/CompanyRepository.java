package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findAllByAppUsersIn(List<AppUser> appUsers);

    Optional<Company> findByIdAndAppUsersIn(Long companyId, List<AppUser> appUsers);
}
