package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.Cost;
import com.data.dataxer.models.domain.QCost;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryFactory;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class QCostRepositoryImpl implements QCostRepository{

    private final JPAQueryFactory query;

    public QCostRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Cost> paginate(Pageable pageable, List<Filter> costFilters, List<Long> companyIds) {
        QCost qCost = QCost.cost;

        BooleanBuilder filterConditions = new BooleanBuilder();

        if (!costFilters.isEmpty()) {
            for (Filter filter : costFilters) {
                filterConditions.or(filter.buildCostFilterPredicate());
            }
        }

        QueryResults<Cost> costResults = this.query.selectFrom(qCost)
                .leftJoin(qCost.contact).fetchJoin()
                .where(qCost.company.id.in(companyIds))
                .where(filterConditions)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(qCost.id.desc())
                .fetchResults();

        return new PageImpl<>(costResults.getResults(), pageable, costResults.getTotal());
    }
}
