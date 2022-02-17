package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.*;
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
    public Page<Pack> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long appProfileId) {
        DefaultSortParser sortParser = new DefaultSortParser();
        DefaultFilterParser filterParser = new DefaultFilterParser();
        Predicate predicate = new BooleanBuilder();

        QPack qPack = QPack.pack;

        Map<String, Path> pathMapping = ImmutableMap.<String, Path>builder()
                .put("pack.id", QPack.pack.id)
                .put("pack.title", QPack.pack.title)
                .build();

        if (!rqlFilter.equals("")) {
            predicate = filterParser.parse(rqlFilter, withBuilderAndParam(new QuerydslFilterBuilder(), new QuerydslFilterParam()
                    .setMapping(pathMapping)));
        }
        OrderSpecifierList orderSpecifierList = sortParser.parse(sortExpression, QuerydslSortContext.withMapping(pathMapping));

        List<Pack> packList = this.query.selectFrom(qPack)
                .where(predicate)
                .where(qPack.appProfile.id.eq(appProfileId))
                .orderBy(orderSpecifierList.getOrders().toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(packList, pageable, getTotalCount(predicate));
    }

    @Override
    public Pack getById(Long id, Long appProfileId) {
        return query.selectFrom(QPack.pack)
                .where(QPack.pack.appProfile.id.eq(appProfileId))
                .where(QPack.pack.id.eq(id))
                .leftJoin(QPack.pack.packItems, QPackItem.packItem).orderBy(QPackItem.packItem.position.desc()).fetchJoin()
                .leftJoin(QPackItem.packItem.item, QItem.item).fetchJoin()
                .leftJoin(QItem.item.itemPrices, QItemPrice.itemPrice).fetchJoin()
                .leftJoin(QItem.item.category, QCategory.category).fetchJoin()
                .fetchOne();
    }

    @Override
    public List<Pack> search(String q, Long appProfileId) {
        QPack qPack = QPack.pack;

        return query.selectFrom(qPack)
                .where(qPack.appProfile.id.eq(appProfileId))
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
