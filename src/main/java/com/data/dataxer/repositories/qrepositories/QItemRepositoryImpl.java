package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.domain.QCategory;
import com.data.dataxer.models.domain.QContact;
import com.data.dataxer.models.domain.QItem;
import com.data.dataxer.models.domain.QItemPrice;
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
public class QItemRepositoryImpl implements QItemRepository {
    private final JPAQueryFactory query;

    public QItemRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Item getById(long id, Long companyId) {
        QItem qItem = QItem.item;
        QItemPrice qItemPrice = QItemPrice.itemPrice;
        QCategory qCategory = QCategory.category;
        QContact qContact = QContact.contact;

        return query
                .selectFrom(qItem)
                .where(qItem.company.id.eq(companyId))
                .where(qItem.id.eq(id))
                .leftJoin(qItem.itemPrices, qItemPrice).fetchJoin()
                .leftJoin(qItem.category, qCategory).fetchJoin()
                .leftJoin(qItem.supplier, qContact).fetchJoin()
                .fetchOne();
    }

    @Override
    public Optional<List<Item>> findAllByTitleContainsAndCompanyIdIn(String q, Long companyId) {
        QItem qItem = QItem.item;

        return Optional.ofNullable(query
                .selectFrom(qItem)
                .where(qItem.company.id.eq(companyId))
                .where(qItem.title.containsIgnoreCase(q))
                .leftJoin(qItem.itemPrices).fetchJoin()
                .fetch());
    }

    @Override
    public Page<Item> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long companyId) {
        DefaultSortParser sortParser = new DefaultSortParser();
        DefaultFilterParser filterParser = new DefaultFilterParser();
        Predicate predicate = new BooleanBuilder();

        QItem qItem = QItem.item;

        Map<String, Path> pathMapping = ImmutableMap.<String, Path>builder()
                .put("item.id", QItem.item.id)
                .build();

        if (!rqlFilter.equals("")) {
            predicate = filterParser.parse(rqlFilter, withBuilderAndParam(new QuerydslFilterBuilder(), new QuerydslFilterParam()
                    .setMapping(pathMapping)));
        }
        OrderSpecifierList orderSpecifierList = sortParser.parse(sortExpression, QuerydslSortContext.withMapping(pathMapping));

        List<Item> itemList = this.query.selectFrom(qItem)
                .leftJoin(QItem.item.itemPrices).fetchJoin()
                .where(predicate)
                .where(qItem.company.id.eq(companyId))
                .orderBy(orderSpecifierList.getOrders().toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(itemList, pageable, getTotalCount(predicate));
    }

    private long getTotalCount(Predicate predicate) {
        QItem qItem = QItem.item;

        return this.query.selectFrom(qItem)
                .where(predicate)
                .fetchCount();
    }
}
