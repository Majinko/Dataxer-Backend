package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Company;

import java.util.List;

public interface QCompanyRepository {
    List<Company> findAllByUser();
}
