package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.QSalary;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigDecimal;

@Repository
public class QSalaryRepositoryImpl implements QSalaryRepository {

    private final JPAQueryFactory query;

    public QSalaryRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public BigDecimal getPriceFromSalaryByUserFinishIsNull(AppUser user, Long companyId) {
        return this.query.select(QSalary.salary.price)
                .from(QSalary.salary)
                .where(QSalary.salary.user.eq(user))
                .where(QSalary.salary.finish.isNull())
                .where(QSalary.salary.company.id.eq(companyId))
                .fetchOne();
    }
}
