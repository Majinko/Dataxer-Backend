package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.QSalary;
import com.data.dataxer.models.domain.Salary;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

@Repository
public class QSalaryRepositoryImpl implements QSalaryRepository {

    private final JPAQueryFactory query;

    public QSalaryRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Salary getActiveSalary(AppUser user, Long apProfileId) {
        return this.query.select(QSalary.salary)
                .from(QSalary.salary)
                .where(QSalary.salary.user.eq(user))
                .where(QSalary.salary.finish.isNull())
                .where(QSalary.salary.appProfile.id.eq(apProfileId))
                .fetchOne();
    }

    @Override
    public List<Salary> getSalariesForMonthAndYearByUserIds(List<String> userIds, LocalDate fromDate, LocalDate toDate, Long apProfileId) {
        BooleanBuilder finishDateCondition = new BooleanBuilder();
        finishDateCondition.and(QSalary.salary.finish.goe(toDate));
        finishDateCondition.or(QSalary.salary.finish.isNull());

        return this.query.selectFrom(QSalary.salary)
                .leftJoin(QSalary.salary.user).fetchJoin()
                .where(QSalary.salary.appProfile.id.eq(apProfileId))
                .where(QSalary.salary.start.loe(fromDate))
                .where(finishDateCondition)
                .where(QSalary.salary.user.uid.in(userIds))
                .orderBy(QSalary.salary.start.desc(), QSalary.salary.finish.desc())
                .groupBy(QSalary.salary.user.id, QSalary.salary.id)
                .fetch();
    }

}
