package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Company;
import com.data.dataxer.models.domain.QCompany;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

public class QCompanyRepositoryImpl implements QCompanyRepository {
    private final JPAQueryFactory query;

    public QCompanyRepositoryImpl(JPAQueryFactory query) {
        this.query = query;
    }

    @Override
    public List<Company> findAllByUser() {
        return null;
    }
}
