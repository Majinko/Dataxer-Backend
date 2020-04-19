package com.data.dataxer.qrepositores;

import com.data.dataxer.models.domain.Company;
import com.data.dataxer.models.domain.QCompany;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

public class QCompanyRepositoryImpl implements QCompanyRepository {
    private final JPAQueryFactory query;
    private QCompany COMAPNY = QCompany.company;

    public QCompanyRepositoryImpl(JPAQueryFactory query) {
        this.query = query;
    }

    @Override
    public List<Company> findAllByUser() {
        return null;
    }
}
