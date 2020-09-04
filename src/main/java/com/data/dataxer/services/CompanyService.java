package com.data.dataxer.services;

import com.data.dataxer.models.domain.Company;

import java.util.List;
import java.util.Optional;

public interface CompanyService {
    Company store(Company company);

    List<Company> findAll();

    Company findById(Long id);

    Company update(Company company, Long id);

    Company getDefaultCompany();
}
