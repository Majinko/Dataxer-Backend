package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Cost;
import com.data.dataxer.models.domain.QCost;
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
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.vineey.rql.filter.FilterContext.withBuilderAndParam;

@Repository
public class QCostRepositoryImpl implements QCostRepository {

    private final JPAQueryFactory query;
    private final EntityManager entityManager;

    public QCostRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager.getEntityManagerFactory().createEntityManager();
        this.query = new JPAQueryFactory(this.entityManager);
    }

    @Override
    public Page<Cost> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long companyId, Boolean disableFilter) {
        DefaultSortParser sortParser = new DefaultSortParser();
        DefaultFilterParser filterParser = new DefaultFilterParser();
        Predicate predicate = new BooleanBuilder();

        QCost qCost = QCost.cost;

        Map<String, Path> pathMapping = ImmutableMap.<String, Path>builder()
                .put("cost.id", QCost.cost.id)
                .build();

        if (!rqlFilter.equals("")) {
            predicate = filterParser.parse(rqlFilter, withBuilderAndParam(new QuerydslFilterBuilder(), new QuerydslFilterParam()
                    .setMapping(pathMapping)));
        }
        OrderSpecifierList orderSpecifierList = sortParser.parse(sortExpression, QuerydslSortContext.withMapping(pathMapping));

        if (!disableFilter) {
            this.entityManager.unwrap(Session.class).enableFilter("companyCondition").setParameter("companyId", companyId);
        }

        List<Cost> costList = this.query.selectFrom(qCost)
                .leftJoin(qCost.contact).fetchJoin()
                .leftJoin(qCost.project).fetchJoin()
                .where(predicate)
                .orderBy(orderSpecifierList.getOrders().toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(costList, pageable, getTotalCount(predicate));
    }

    @Override
    public Optional<Cost> getById(Long id, Long companyId, Boolean disableFilter) {
        if (!disableFilter) {
            this.entityManager.unwrap(Session.class).enableFilter("companyCondition").setParameter("companyId", companyId);
        }

        return Optional.ofNullable(
                this.constructGetAllByIdAndCompanyIds(id).fetchOne()
        );
    }

    @Override
    public Optional<Cost> getByIdWithRelation(Long id, Long companyId, Boolean disableFilter) {
        if (!disableFilter) {
            this.entityManager.unwrap(Session.class).enableFilter("companyCondition").setParameter("companyId", companyId);
        }

        return Optional.ofNullable(
                this.constructGetAllByIdAndCompanyIds(id)
                        .leftJoin(QCost.cost.category).fetchJoin()
                        .leftJoin(QCost.cost.contact).fetchJoin()
                        .leftJoin(QCost.cost.project).fetchJoin()
                        .leftJoin(QCost.cost.files).fetchJoin()
                        .fetchOne()
        );
    }

    private JPAQuery<Cost> constructGetAllByIdAndCompanyIds(Long id) {
        return query.selectFrom(QCost.cost)
                .where(QCost.cost.id.eq(id));
    }

    private long getTotalCount(Predicate predicate) {
        QCost qCost = QCost.cost;

        return this.query.selectFrom(qCost)
                .where(predicate)
                .fetchCount();
    }
}
