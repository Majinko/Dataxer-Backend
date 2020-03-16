package com.data.dataxer.qrepositores;

import com.data.dataxer.models.domain.Company;

import java.util.List;

public interface QCompanyRepository {
    List<Company> findAllByUser();
}
