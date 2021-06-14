package com.data.dataxer.repositories.qrepositories;


import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.domain.QCategory;
import com.data.dataxer.models.domain.QTask;
import com.data.dataxer.models.domain.QTime;
import com.github.vineey.rql.filter.parser.DefaultFilterParser;
import com.github.vineey.rql.querydsl.filter.QuerydslFilterBuilder;
import com.github.vineey.rql.querydsl.filter.QuerydslFilterParam;
import com.github.vineey.rql.querydsl.sort.OrderSpecifierList;
import com.github.vineey.rql.querydsl.sort.QuerydslSortContext;
import com.github.vineey.rql.sort.parser.DefaultSortParser;
import com.google.common.collect.ImmutableMap;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.StreamUtils;
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
                .join(QTime.time1.category).fetchJoin()
                .where(QTime.time1.dateWork.between(from, to))
                .where(QTime.time1.company.id.eq(companyId))
                .where(QTime.time1.user.id.eq(userId))
                .orderBy(QTime.time1.dateWork.desc())
                .fetch();
    }

    @Override
    public List<Tuple> getAllUserMonths(Long userId, Long companyId) {
        return this.query.from(QTime.time1)
                .select(QTime.time1.dateWork.year(), QTime.time1.dateWork.month())
                .where(QTime.time1.user.id.eq(userId))
                .where(QTime.time1.company.id.eq(companyId))
                .groupBy(QTime.time1.dateWork.year())
                .groupBy(QTime.time1.dateWork.month())
                .orderBy(QTime.time1.dateWork.year().desc())
                .orderBy(QTime.time1.dateWork.month().desc())
                .fetch();
    }

    @Override
    public List<Project> getAllUserProjects(Long userId, Long companyId) {
        return this.query.from(QTime.time1)
                .select(QTime.time1.project)
                .leftJoin(QTime.time1.project).fetchJoin()
                .where(QTime.time1.user.id.eq(userId))
                .where(QTime.time1.company.id.eq(companyId))
                .groupBy(QTime.time1.project)
                .orderBy(QTime.time1.project.id.asc())
                .fetch();
    }

    @Override
    public List<Tuple> getUserLastProjects(Long userId, Long limit, Long companyId) {
        return this.query.selectDistinct(QTime.time1.project, QTime.time1.dateWork, QTime.time1.id)
                .from(QTime.time1)
                .leftJoin(QTime.time1.project)
                .where(QTime.time1.user.id.eq(userId))
                .where(QTime.time1.company.id.eq(companyId))
                .orderBy(QTime.time1.dateWork.desc(), QTime.time1.id.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Tuple> getProjectLastCategories(Long projectId, Long limit, Long companyId) {
        return this.query.selectDistinct(QTime.time1.category, QTime.time1.dateWork, QTime.time1.id)
                .from(QTime.time1)
                .leftJoin(QTime.time1.category)
                .where(QTime.time1.project.id.eq(projectId))
                .where(QTime.time1.company.id.eq(companyId))
                .orderBy(QTime.time1.dateWork.desc(), QTime.time1.id.desc())
                .limit(limit)
                .fetch();
    }

    public LocalDate getUserFirstLastRecord(Long userId, Long companyId, Boolean last) {
        JPAQuery<LocalDate> jpaQuery = this.query.select(QTime.time1.dateWork)
                .from(QTime.time1)
                .where(QTime.time1.company.id.eq(companyId))
                .where(QTime.time1.user.id.eq(userId))
                .limit(1);

        if (last) {
            List<LocalDate> dates = jpaQuery.orderBy(QTime.time1.dateWork.desc()).fetch();

            if (!dates.isEmpty()) {
                return dates.get(0);
            }
        } else {
            List<LocalDate> dates = jpaQuery.orderBy(QTime.time1.dateWork.asc()).fetch();

            if (!dates.isEmpty()) {
                return dates.get(0);
            }
        }

        return null;
    }

    @Override
    public Long getCountProjects(Long id, Long companyId) {
        return this.query
                .select(QTime.time1.project.count())
                .from(QTime.time1)
                .groupBy(QTime.time1.project)
                .where(QTime.time1.company.id.eq(companyId))
                .where(QTime.time1.user.id.eq(id))
                .fetchCount();
    }

    @Override
    public Integer sumUserTime(Long userId, Long companyId) {
        return this.query
                .from(QTime.time1)
                .select(QTime.time1.time.sum())
                .where(QTime.time1.company.id.eq(companyId))
                .where(QTime.time1.user.id.eq(userId))
                .fetchOne();
    }

    @Override
    public List<Time> getAllTimeRecords(Long companyId) {
        return this.query.selectFrom(QTime.time1)
                .leftJoin(QTime.time1.user).fetchJoin()
                .where(QTime.time1.company.id.eq(companyId))
                .fetch();
    }

    @Override
    public List<Integer> getAllYears(Long companyId) {
        return this.query.select(QTime.time1.dateWork.year())
                .from(QTime.time1)
                .groupBy(QTime.time1.dateWork.year())
                .where(QTime.time1.company.id.eq(companyId))
                .orderBy(QTime.time1.dateWork.year().desc())
                .fetch();
    }

    @Override
    public Tuple getUserProjectCategoryHoursAndPrice(Long projectId, String userUid, List<Long> categoryIds, Long companyId) {
        return this.query.select(QTime.time1.time.sum(), QTime.time1.price.sum())
                .from(QTime.time1)
                .where(QTime.time1.project.id.eq(projectId))
                .where(QTime.time1.user.uid.eq(userUid))
                .where(QTime.time1.category.id.in(categoryIds))
                .where(QTime.time1.company.id.eq(companyId))
                .fetchOne();
    }

    @Override
    public Integer getUserProjectTimeBetweenYears(Integer startYear, Integer endYear, String uid, Long companyId) {
        return this.query.select(QTime.time1.time.sum())
                .from(QTime.time1)
                .where(QTime.time1.user.uid.eq(uid))
                .where(QTime.time1.company.id.eq(companyId))
                .where(QTime.time1.salary.isNotNull())
                .where(QTime.time1.dateWork.year().goe(startYear))
                .where(QTime.time1.dateWork.year().loe(endYear))
                .fetchOne();
    }

    @Override
    public List<Tuple> getUserActiveMonths(Integer startYear, Integer endYear, String uid, Long companyId) {
        return this.query.select(QTime.time1.dateWork.month(), QTime.time1.dateWork.year(), QTime.time1.user.uid)
                .from(QTime.time1)
                .leftJoin(QTime.time1.salary)
                .where(QTime.time1.user.uid.eq(uid))
                .where(QTime.time1.company.id.eq(companyId))
                .where(QTime.time1.dateWork.year().goe(startYear))
                .where(QTime.time1.dateWork.year().loe(endYear))
                .where(QTime.time1.salary.isActive.eq(Boolean.TRUE))
                .groupBy(QTime.time1.dateWork.month())
                .groupBy(QTime.time1.dateWork.year())
                .groupBy(QTime.time1.user.uid)
                .fetch();
    }

    @Override
    public List<Tuple> getProjectAllUsersActiveMonth(Integer startYear, Integer endYear, Long companyId) {
        return this.query.select(QTime.time1.dateWork.month(), QTime.time1.dateWork.year(), QTime.time1.user.uid)
                .from(QTime.time1)
                .leftJoin(QTime.time1.salary)
                .where(QTime.time1.company.id.eq(companyId))
                .where(QTime.time1.dateWork.year().goe(startYear))
                .where(QTime.time1.dateWork.year().loe(endYear))
                .where(QTime.time1.salary.isActive.eq(Boolean.TRUE))
                .groupBy(QTime.time1.dateWork.month())
                .groupBy(QTime.time1.dateWork.year())
                .groupBy(QTime.time1.user.uid)
                .fetch();
    }

    @Override
    public List<Tuple> getAllProjectUsers(Long id, Long companyId) {
        return this.query.select(QTime.time1.user.uid, QTime.time1.user.firstName, QTime.time1.user.lastName)
                .from(QTime.time1)
                .where(QTime.time1.project.id.eq(id))
                .where(QTime.time1.company.id.eq(companyId))
                .groupBy(QTime.time1.user.uid)
                .groupBy(QTime.time1.user.firstName)
                .groupBy(QTime.time1.user.lastName)
                .fetch();
    }

    @Override
    public List<Tuple> getProjectUsersTimePriceSums(Long id, Long companyId) {
        return this.query.select(QTime.time1.time.sum(), QTime.time1.price.sum(), QTime.time1.user.uid,
                QTime.time1.user.firstName, QTime.time1.user.lastName)
                .from(QTime.time1)
                .where(QTime.time1.project.id.eq(id))
                .where(QTime.time1.project.company.id.eq(companyId))
                .where(QTime.time1.user.uid.isNotNull())
                .groupBy(QTime.time1.user.uid)
                .groupBy(QTime.time1.user.firstName)
                .groupBy(QTime.time1.user.lastName)
                .fetch();
    }

    @Override
    public List<Tuple> getAllProjectUserCategoryData(Long id, List<Long> categoryIds, Long companyId) {
        return this.query.select(QTime.time1.user.uid, QTime.time1.user.firstName, QTime.time1.user.lastName,
                QTime.time1.time.sum(), QTime.time1.price.sum())
                .from(QTime.time1)
                .leftJoin(QTime.time1.user)
                .where(QTime.time1.project.id.eq(id))
                .where(QTime.time1.category.id.in(categoryIds))
                .where(QTime.time1.company.id.eq(companyId))
                .where(QTime.time1.user.uid.isNotNull())
                .groupBy(QTime.time1.user.uid)
                .groupBy(QTime.time1.user.firstName)
                .groupBy(QTime.time1.user.lastName)
                .fetch();
    }

    @Override
    public List<Time> getProjectAllUsersTimes(Long id, Category category, LocalDate dateFrom, LocalDate dateTo, String userUid, Long companyId) {
        BooleanBuilder predicate = new BooleanBuilder();
        BooleanBuilder userPredicate = new BooleanBuilder();
        if (category != null) {
            predicate.and(QTime.time1.category.id.eq(category.getId()));
        }
        if (!userUid.isEmpty()) {
            userPredicate.and(QTime.time1.user.uid.eq(userUid));
        }
        return this.query.selectFrom(QTime.time1)
                .leftJoin(QTime.time1.category).fetchJoin()
                .leftJoin(QTime.time1.user).fetchJoin()
                .where(QTime.time1.project.id.eq(id))
                .where(predicate)
                .where(userPredicate)
                .where(QTime.time1.dateWork.goe(dateFrom))
                .where(QTime.time1.dateWork.loe(dateTo))
                .where(QTime.time1.company.id.eq(companyId))
                .orderBy(QTime.time1.dateWork.desc())
                .orderBy(QTime.time1.user.id.desc())
                .fetch();
    }

    @Override
    public Integer getTotalProjectTimeForYear(Long id, Integer year, Long companyId) {
        return this.query.select(QTime.time1.time.sum())
                .from(QTime.time1)
                .where(QTime.time1.project.id.eq(id))
                .where(QTime.time1.dateWork.year().eq(year))
                .where(QTime.time1.company.id.eq(companyId))
                .fetchOne();
    }

    @Override
    public List<Integer> getProjectYears(Long id, Long companyId) {
        return this.query.select(QTime.time1.dateWork.year())
                .from(QTime.time1)
                .where(QTime.time1.project.id.eq(id))
                .where(QTime.time1.company.id.eq(companyId))
                .groupBy(QTime.time1.dateWork.year())
                .orderBy(QTime.time1.dateWork.year().asc())
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
