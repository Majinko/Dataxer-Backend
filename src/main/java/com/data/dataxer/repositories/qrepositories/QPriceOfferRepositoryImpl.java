package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.domain.QDocumentPack;
import com.data.dataxer.models.domain.QDocumentPackItem;
import com.data.dataxer.models.domain.QInvoice;
import com.data.dataxer.models.domain.QItem;
import com.data.dataxer.models.domain.QPriceOffer;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.github.vineey.rql.filter.FilterContext.withBuilderAndParam;

@Repository
public class QPriceOfferRepositoryImpl implements QPriceOfferRepository {
    private final JPAQueryFactory query;

    public QPriceOfferRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<PriceOffer> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long companyId) {
        DefaultSortParser sortParser = new DefaultSortParser();
        DefaultFilterParser filterParser = new DefaultFilterParser();
        Predicate predicate = new BooleanBuilder();

        Map<String, Path> pathMapping = ImmutableMap.<String, Path>builder()
                .put("priceOffer.id", QPriceOffer.priceOffer.id)
                .put("priceOffer.state", QPriceOffer.priceOffer.state)
                .put("priceOffer.contact.id", QPriceOffer.priceOffer.contact.id)
                .put("priceOffer.contact.name", QPriceOffer.priceOffer.contact.name)
                .build();

        if (!rqlFilter.equals("")) {
            predicate = filterParser.parse(rqlFilter, withBuilderAndParam(new QuerydslFilterBuilder(), new QuerydslFilterParam()
                    .setMapping(pathMapping)));
        }

        OrderSpecifierList orderSpecifierList = sortParser.parse(sortExpression, QuerydslSortContext.withMapping(pathMapping));

        List<PriceOffer> priceOfferList = query.selectFrom(QPriceOffer.priceOffer)
                .leftJoin(QPriceOffer.priceOffer.contact).fetchJoin()
                .leftJoin(QPriceOffer.priceOffer.project).fetchJoin()
                .where(predicate)
                .orderBy(orderSpecifierList.getOrders().toArray(new OrderSpecifier[0]))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        return new PageImpl<>(priceOfferList, pageable, getTotalCount(predicate));
    }

    @Override
    public Optional<PriceOffer> getById(Long id, Long companyId) {
        PriceOffer priceOffer = query.selectFrom(QPriceOffer.priceOffer)
                .leftJoin(QPriceOffer.priceOffer.contact).fetchJoin()
                .leftJoin(QPriceOffer.priceOffer.project).fetchJoin()
                .leftJoin(QPriceOffer.priceOffer.packs, QDocumentPack.documentPack).fetchJoin()
                .where(QPriceOffer.priceOffer.id.eq(id))
                .orderBy(QDocumentPack.documentPack.position.asc())
                .fetchOne();

        // price offer pack set items
        if (priceOffer != null) {
            priceOfferPackSetItems(priceOffer);
        }

        return Optional.ofNullable(priceOffer);
    }

    @Override
    public Optional<PriceOffer> getByIdSimple(Long id, Long companyId) {
        QPriceOffer qPriceOffer = QPriceOffer.priceOffer;

        return Optional.ofNullable(query.selectFrom(qPriceOffer)
                .where(qPriceOffer.id.eq(id))
                .where(qPriceOffer.company.id.eq(companyId))
                .fetchOne());
    }

    private void priceOfferPackSetItems(PriceOffer priceOffer) {
        QDocumentPackItem qDocumentPackItem = QDocumentPackItem.documentPackItem;
        QItem qItem = QItem.item;

        List<DocumentPackItem> priceOfferPackItems = query.selectFrom(qDocumentPackItem)
                .where(qDocumentPackItem.pack.id.in(priceOffer.getPacks().stream().map(DocumentPack::getId).collect(Collectors.toList())))
                .leftJoin(qDocumentPackItem.item, qItem).fetchJoin()
                .orderBy(qDocumentPackItem.position.asc())
                .fetch();

        // price offer pack set items
        priceOffer.getPacks().forEach(documentPack -> documentPack.setPackItems(
                priceOfferPackItems.stream().filter(
                        priceOfferPackItem -> priceOfferPackItem.getPack().getId().equals(documentPack.getId())).collect(Collectors.toList())
        ));
    }

    private Long getTotalCount(Predicate predicate) {
        QPriceOffer qPriceOffer = QPriceOffer.priceOffer;

        return query.selectFrom(qPriceOffer)
                .where(predicate)
                .fetchCount();
    }

}
