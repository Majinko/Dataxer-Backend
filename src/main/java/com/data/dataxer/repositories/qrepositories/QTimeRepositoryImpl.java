package com.data.dataxer.repositories.qrepositories;


import com.data.dataxer.models.domain.QTask;
import com.data.dataxer.models.domain.QTime;
import com.data.dataxer.models.domain.Time;
import com.github.vineey.rql.filter.parser.DefaultFilterParser;
import com.github.vineey.rql.querydsl.filter.QuerydslFilterBuilder;
import com.github.vineey.rql.querydsl.filter.QuerydslFilterParam;
import com.github.vineey.rql.querydsl.sort.OrderSpecifierList;
import com.github.vineey.rql.querydsl.sort.QuerydslSortContext;
import com.github.vineey.rql.sort.parser.DefaultSortParser;
import com.google.common.collect.ImmutableMap;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.vineey.rql.filter.FilterContext.withBuilderAndParam;
import static com.github.vineey.rql.querydsl.filter.QueryDslFilterContext.withMapping;

@Repository
public class QTimeRepositoryImpl implements QTimeRepository {

    private final JPAQueryFactory query;

    public QTimeRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<Time> getById(Long id, Long userId, Long companyId) {
        QTime qTime = QTime.time1;

        return Optional.ofNullable(this.query.selectFrom(qTime)
                .leftJoin(qTime.category).fetchJoin()
                .leftJoin(qTime.user).fetchJoin()
                .leftJoin(qTime.company).fetchJoin()
                .leftJoin(qTime.project).fetchJoin()
                .where(qTime.id.eq(id))
                .where(qTime.company.id.eq(companyId))
                .where(qTime.user.id.eq(userId))
                .fetchOne());
    }

    @Override
    public Optional<Time> getByIdSimple(Long id, Long userId, Long companyId) {
        QTime qTime = QTime.time1;
        return Optional.ofNullable(this.query.selectFrom(qTime)
                .where(qTime.id.eq(id))
                .where(qTime.company.id.eq(companyId))
                .where(qTime.user.id.eq(userId))
                .fetchOne());
    }

    @Override
    public Page<Time> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long userId, Long companyId) {
        DefaultSortParser sortParser = new DefaultSortParser();
        DefaultFilterParser filterParser = new DefaultFilterParser();
        Predicate predicate = new BooleanBuilder();

        QTime qTime = QTime.time1;

        Map<String, Path> pathMapping = ImmutableMap.<String, Path>builder()
                .put("time.id", QTask.task.id)
                .build();

        if (!rqlFilter.equals("")) {
            predicate = filterParser.parse(rqlFilter, withBuilderAndParam(new QuerydslFilterBuilder(), new QuerydslFilterParam()
                    .setMapping(pathMapping)));
        }
        OrderSpecifierList orderSpecifierList = sortParser.parse(sortExpression, QuerydslSortContext.withMapping(pathMapping));

        List<Time> timeList = this.query.selectFrom(qTime)
                .leftJoin(qTime.category).fetchJoin()
                .where(predicate)
                .where(qTime.company.id.eq(companyId))
                .where(qTime.user.id.eq(userId))
                .orderBy(orderSpecifierList.getOrders().toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(timeList, pageable, getTotalCount(predicate));
    }

    @Override
    public List<Time> getHourOverviewForAllUsers(LocalDate fromDate, LocalDate toDate, Long companyId) {
        return this.query.selectFrom(QTime.time1)
                .leftJoin(QTime.time1.user).fetchJoin()
                .where(QTime.time1.company.id.eq(companyId))
                .where(QTime.time1.dateWork.between(fromDate, toDate))
                .fetch();
    }

    @Override
    public List<Time> allForPeriod(LocalDate from, LocalDate to, Long userId, Long companyId) {
        return this.query.selectFrom(QTime.time1)
                .leftJoin(QTime.time1.user).fetchJoin()
                .leftJoin(QTime.time1.project).fetchJoin()
                .leftJoin(QTime.time1.category).fetchJoin()
                .where(QTime.time1.dateWork.between(from, to))
                .where(QTime.time1.company.id.eq(companyId))
                .where(QTime.time1.user.id.eq(userId))
                .orderBy(QTime.time1.id.desc())
                .fetch();
    }

    @Override
    public List<Time> allUserTimesForPeriod(LocalDate from, LocalDate to, Long userId, Long companyId) {
        return this.query.selectFrom(QTime.time1)
                .leftJoin(QTime.time1.user).fetchJoin()
                .leftJoin(QTime.time1.project).fetchJoin()
                .where(QTime.time1.user.id.eq(userId))
                .where(QTime.time1.dateWork.between(from, to))
                .where(QTime.time1.company.id.eq(companyId))
                .fetch();
    }

    @Override
    public List<Time> getUserLastProjects(Long userId, Long offset, Long limit, Long companyId) {
        return this.query.selectFrom(QTime.time1)
                .leftJoin(QTime.time1.project).fetchJoin()
                .where(QTime.time1.user.id.eq(userId))
                .where(QTime.time1.company.id.eq(companyId))
                .orderBy(QTime.time1.dateWork.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Time> getTimesForProjectCategoryOrderByDate(Long projectId, Long companyId) {
        return this.query.selectFrom(QTime.time1)
                .leftJoin(QTime.time1.category).fetchJoin()
                .where(QTime.time1.project.id.eq(projectId))
                .where(QTime.time1.company.id.eq(companyId))
                .orderBy(QTime.time1.dateWork.desc())
                .groupBy(QTime.time1.category)
                .fetch();
    }

    @Override
    public List<Time> getTimesForProjectCategoryOrderByPosition(Long projectId, Long companyId) {
        return this.query.selectFrom(QTime.time1)
                .leftJoin(QTime.time1.category).fetchJoin()
                .where(QTime.time1.project.id.eq(projectId))
                .where(QTime.time1.company.id.eq(companyId))
                .orderBy(QTime.time1.category.position.asc())
                .groupBy(QTime.time1.category)
                .fetch();
    }

    private long getTotalCount(Predicate predicate) {
        QTime qTime = QTime.time1;

        return this.query.selectFrom(qTime)
                .where(predicate)
                .fetchCount();
    }

    public Predicate predicate(String rqlFilter) {
        DefaultFilterParser filterParser = new DefaultFilterParser();

        Map<String, Path> pathHashMap = ImmutableMap.<String, Path>builder()
                .put("time.description", QTime.time1.description)
                .put("time.dateWork", QTime.time1.dateWork)
                .put("time.categoryId", QTime.time1.category.id)
                .build();

        return filterParser.parse(rqlFilter, withMapping(pathHashMap));
    }
}
