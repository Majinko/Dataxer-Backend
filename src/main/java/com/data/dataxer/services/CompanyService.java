package com.data.dataxer.services;

import com.data.dataxer.models.domain.Company;

import java.util.List;

public interface CompanyService {
    Company store(Company company);

    List<Company> findAll();

    Company findById(Long id);

    Company update(Company company);

    Company getDefaultCompany();

    void destroy(Long id);

    void switchCompany(Long id);
}
