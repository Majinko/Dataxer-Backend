package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.domain.QItem;
import com.data.dataxer.models.domain.QItemPrice;
import com.data.dataxer.models.domain.QPack;
import com.data.dataxer.models.domain.QPackItem;
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

import static com.github.vineey.rql.filter.FilterContext.withBuilderAndParam;

@Repository
public class QPackRepositoryImpl implements QPackRepository {
    private final JPAQueryFactory query;

    public QPackRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Pack> paginate(Pageable pageable, String rqlFilter, String sortExpression, List<Long> companyIds) {
        DefaultSortParser sortParser = new DefaultSortParser();
        DefaultFilterParser filterParser = new DefaultFilterParser();
        Predicate predicate = new BooleanBuilder();

        QPack qPack = QPack.pack;

        Map<String, Path> pathMapping = ImmutableMap.<String, Path>builder()
                .put("pack.id", QPack.pack.id)
                .build();

        if (!rqlFilter.equals("")) {
            predicate = filterParser.parse(rqlFilter, withBuilderAndParam(new QuerydslFilterBuilder(), new QuerydslFilterParam()
                    .setMapping(pathMapping)));
        }
        OrderSpecifierList orderSpecifierList = sortParser.parse(sortExpression, QuerydslSortContext.withMapping(pathMapping));

        List<Pack> packList = this.query.selectFrom(qPack)
                .where(predicate)
                .where(qPack.company.id.in(companyIds))
                .orderBy(orderSpecifierList.getOrders().toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(packList, pageable, getTotalCount(predicate));
    }

    @Override
    public Pack getById(Long id, List<Long> companyIds) {
        QPack qPack = QPack.pack;
        QPackItem qPackItem = QPackItem.packItem;
        QItem qItem = QItem.item;
        QItemPrice qItemPrice = QItemPrice.itemPrice;

        return query.selectFrom(qPack)
                .where(qPack.company.id.in(companyIds))
                .where(qPack.id.eq(id))
                .leftJoin(qPack.packItems, qPackItem).orderBy(qPackItem.position.desc()).fetchJoin()
                .leftJoin(qPackItem.item, qItem).fetchJoin()
                .leftJoin(qItem.itemPrices, qItemPrice).fetchJoin()
                .fetchOne();
    }

    @Override
    public List<Pack> search(String q, List<Long> companyIds) {
        QPack qPack = QPack.pack;

        return query.selectFrom(qPack)
                .where(qPack.company.id.in(companyIds))
                .where(qPack.title.containsIgnoreCase(q))
                .fetch();
    }

    private long getTotalCount(Predicate predicate) {
        QPack qPack = QPack.pack;

        return this.query.selectFrom(qPack)
                .where(predicate)
                .fetchCount();
    }
}
