package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.QTime;
import com.data.dataxer.models.domain.Time;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

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
    public Page<Time> paginate(Pageable pageable, Filter filter, Long userId, List<Long> companyIds) {
        QTime qTime = QTime.time1;
        BooleanBuilder filterCondition = new BooleanBuilder();

        List<Time> times = this.query.selectFrom(qTime)
                .leftJoin(qTime.category).fetchJoin()
                .where(filterCondition)
                .where(qTime.company.id.in(companyIds))
                .where(qTime.user.id.eq(userId))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(qTime.id.desc())
                .fetch();

        return new PageImpl<Time>(times, pageable, total());
    }

    private Long total() {
        QTime qTime = QTime.time1;
        return this.query.selectFrom(qTime).fetchCount();
    }
}
