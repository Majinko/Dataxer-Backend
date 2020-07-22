package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.QAppUser;
import com.data.dataxer.models.domain.QCompany;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class QAppUserRepositoryImpl implements QAppUserRepository {
    private final JPAQueryFactory query;

    public QAppUserRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<AppUser> all(List<Long> companyIds) {
        QAppUser qAppUser = QAppUser.appUser;
        QCompany qCompany = QCompany.company;

        return query.selectFrom(qAppUser)
                .where(qAppUser.id.in(
                        JPAExpressions.select(qCompany.appUser.id).from(qCompany).where(qCompany.id.in(companyIds))
                ))
                .fetch();
    }
}
