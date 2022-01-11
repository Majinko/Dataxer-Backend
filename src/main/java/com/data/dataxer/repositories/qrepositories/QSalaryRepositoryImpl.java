package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.QSalary;
import com.data.dataxer.models.domain.Salary;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class QSalaryRepositoryImpl implements QSalaryRepository {

    private final JPAQueryFactory query;

    public QSalaryRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Salary getActiveSalary(Long companyId, AppUser user, Long apProfileId) {
        return this.query.select(QSalary.salary)
                .from(QSalary.salary)
                .where(QSalary.salary.company.id.eq(companyId))
                .where(QSalary.salary.user.eq(user))
                .where(QSalary.salary.finish.isNull())
                .where(QSalary.salary.appProfile.id.eq(apProfileId))
                .fetchOne();
    }

    @Override
    public List<Salary> getSalariesForUsersByIds(List<Long> userIds, Long apProfileId) {
        return this.query.selectFrom(QSalary.salary)
                .leftJoin(QSalary.salary.user).fetchJoin()
                .where(QSalary.salary.appProfile.id.eq(apProfileId))
                .where(QSalary.salary.finish.isNull())
                .where(QSalary.salary.user.id.in(userIds))
                .fetch();
    }
}
