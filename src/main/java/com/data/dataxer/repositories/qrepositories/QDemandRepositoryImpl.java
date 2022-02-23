package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Demand;
import com.data.dataxer.models.domain.QDemand;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.vineey.rql.filter.FilterContext.withBuilderAndParam;

@Repository
public class QDemandRepositoryImpl implements QDemandRepository {
    private final JPAQueryFactory query;

    public QDemandRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Demand> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long appProfileId) {
        DefaultSortParser sortParser = new DefaultSortParser();
        DefaultFilterParser filterParser = new DefaultFilterParser();
        Predicate predicate = new BooleanBuilder();

        QDemand qDemand = QDemand.demand;

        Map<String, Path> pathMapping = ImmutableMap.<String, Path>builder()
                .put("demand.id", QDemand.demand.id)
                .build();

        if (!rqlFilter.equals("")) {
            predicate = filterParser.parse(rqlFilter, withBuilderAndParam(new QuerydslFilterBuilder(), new QuerydslFilterParam()
                    .setMapping(pathMapping)));
        }
        OrderSpecifierList orderSpecifierList = sortParser.parse(sortExpression, QuerydslSortContext.withMapping(pathMapping));

        List<Demand> demandList = this.query.selectFrom(qDemand)
                .where(predicate)
                .where(qDemand.appProfile.id.eq(appProfileId))
                .orderBy(orderSpecifierList.getOrders().toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(demandList, pageable, getTotalCount(predicate));
    }

    @Override
    public Optional<Demand> getById(Long id, Long appProfileId) {
        QDemand qDemand = QDemand.demand;

        return Optional.ofNullable(query.selectFrom(qDemand)
                .where(qDemand.appProfile.id.eq(appProfileId))
                .where(qDemand.id.eq(id))
                .fetchOne());
    }

    private long getTotalCount(Predicate predicate) {
        QDemand qDemand = QDemand.demand;

        return this.query.selectFrom(qDemand)
                .where(predicate)
                .fetchCount();
    }
}
