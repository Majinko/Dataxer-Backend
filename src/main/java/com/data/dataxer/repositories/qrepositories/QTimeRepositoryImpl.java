package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.QTime;
import com.data.dataxer.models.domain.Time;
import com.github.vineey.rql.filter.parser.DefaultFilterParser;
import com.google.common.collect.ImmutableMap;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.vineey.rql.querydsl.filter.QueryDslFilterContext.withMapping;

@Repository
public class QTimeRepositoryImpl implements QTimeRepository {

    private final JPAQueryFactory query;

    public QTimeRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<Time> getById(Long id, List<Long> companyIds) {
        QTime qtime = QTime.time1;

        return Optional.ofNullable(this.query.selectFrom(qtime)
                .leftJoin(qtime.category).fetchJoin()
                .leftJoin(qtime.user).fetchJoin()
                .leftJoin(qtime.company).fetchJoin()
                .leftJoin(qtime.project).fetchJoin()
                .where(qtime.id.eq(id))
                .where(qtime.company.id.in(companyIds))
                .fetchOne());
    }

    @Override
    public Optional<Time> getByIdSimple(Long id, List<Long> companyIds) {
        QTime qTime = QTime.time1;
        return Optional.ofNullable(this.query.selectFrom(qTime)
                .where(qTime.id.eq(id))
                .where(qTime.company.id.in(companyIds))
                .fetchOne());
    }

    @Override
    public Page<Time> paginate(Pageable pageable, String rqlFilter, Long userId, List<Long> companyIds) {
        QTime qTime = QTime.time1;
        BooleanBuilder filterCondition = new BooleanBuilder();

        JPAQuery<Time> times = this.query.selectFrom(qTime)
                .leftJoin(qTime.category).fetchJoin()
                .where(filterCondition)
                .where(qTime.company.id.in(companyIds))
                .where(qTime.user.id.eq(userId))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(qTime.id.desc());

        if (rqlFilter != null) {
            times.where(predicate(rqlFilter));
        }

        return new PageImpl<Time>(times.fetch(), pageable, total());
    }

    private Long total() {
        QTime qTime = QTime.time1;
        return this.query.selectFrom(qTime).fetchCount();
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
