package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.domain.QCategory;
import com.data.dataxer.models.domain.QContact;
import com.data.dataxer.models.domain.QItem;
import com.data.dataxer.models.domain.QItemPrice;
import com.data.dataxer.models.domain.QStorage;
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
import io.github.perplexhub.rsql.RSQLQueryDslSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.*;

@Repository
public class QItemRepositoryImpl implements QItemRepository {
    private final JPAQueryFactory query;

    public QItemRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Item getById(long id, Long companyId) {
        Item item = this.constructGetAllByIdAndCompanyId(id, companyId)
                .where(QItem.item.company.id.eq(companyId))
                .where(QItem.item.id.eq(id))
                .leftJoin(QItem.item.itemPrices, QItemPrice.itemPrice).fetchJoin()
                .leftJoin(QItem.item.supplier, QContact.contact).fetchJoin()
                .fetchOne();

        if (item != null) {
            item.setFiles(
                    Objects.requireNonNull(this.constructGetAllByIdAndCompanyId(id, companyId)
                                    .leftJoin(QItem.item.files, QStorage.storage).fetchJoin()
                                    .fetchOne())
                            .getFiles()
            );

            item.setCategories(
                    Objects.requireNonNull(
                                    this.constructGetAllByIdAndCompanyId(id, companyId)
                                            .leftJoin(QItem.item.categories, QCategory.category).fetchJoin()
                                            .fetchOne())
                            .getCategories()
            );
        }

        return item;
    }

    private JPAQuery<Item> constructGetAllByIdAndCompanyId(Long id, Long companyId) {
        return query.selectFrom(QItem.item)
                .where(QItem.item.company.id.eq(companyId))
                .where(QItem.item.id.eq(id));
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
        Predicate predicate = buildPredicate(rqlFilter);
        OrderSpecifierList orderSpecifierList = buildSort(sortExpression);

        List<Item> itemList = this.query.selectFrom(QItem.item)
                //.leftJoin(QItem.item.supplier)
                .leftJoin(QItem.item.itemPrices).fetchJoin()
                .where(predicate)
                .where(QItem.item.company.id.eq(companyId))
                .orderBy(orderSpecifierList.getOrders().toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(itemList, pageable, getTotalCount(predicate));
    }

    private Predicate buildPredicate(String rsqlFilter) {
        Predicate predicate = new BooleanBuilder();

        if (rsqlFilter.equals("")) {
            return predicate;
        }

        Map<String, String> filterPathMapping = new HashMap<>();

        filterPathMapping.put("id", "id");
        filterPathMapping.put("title", "title");
        filterPathMapping.put("code", "code");
        filterPathMapping.put("manufacturer", "manufacturer");
        filterPathMapping.put("contactName", "supplier.name");

        return RSQLQueryDslSupport.toPredicate(rsqlFilter, QItem.item, filterPathMapping);
    }

    private OrderSpecifierList buildSort(String sortExpression) {
        DefaultSortParser sortParser = new DefaultSortParser();

        Map<String, Path> pathMapping = ImmutableMap.<String, Path>builder()
                .put("item.id", QItem.item.id)
                .put("item.title", QItem.item.title)
                .put("item.code", QItem.item.code)
                .put("item.manufacturer", QItem.item.manufacturer)
                .put("item.contact.name", QItem.item.supplier.name)
                .build();

        return sortParser.parse(sortExpression, QuerydslSortContext.withMapping(pathMapping));
    }

    private long getTotalCount(Predicate predicate) {
        QItem qItem = QItem.item;

        return this.query.selectFrom(qItem)
                .where(predicate)
                .fetchCount();
    }
}
