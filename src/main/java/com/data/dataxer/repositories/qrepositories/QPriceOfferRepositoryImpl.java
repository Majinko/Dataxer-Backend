package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.*;
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
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import com.data.dataxer.models.page.CustomPageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.vineey.rql.filter.FilterContext.withBuilderAndParam;

@Repository
public class QPriceOfferRepositoryImpl implements QPriceOfferRepository {
    private final JPAQueryFactory query;

    public QPriceOfferRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<PriceOffer> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long appProfileId) {
        DefaultSortParser sortParser = new DefaultSortParser();
        DefaultFilterParser filterParser = new DefaultFilterParser();
        Predicate predicate = new BooleanBuilder();

        Map<String, Path> pathMapping = ImmutableMap.<String, Path>builder()
                .put("priceOffer.id", QPriceOffer.priceOffer.id)
                .put("priceOffer.company.id", QPriceOffer.priceOffer.company.id)
                .put("priceOffer.title", QPriceOffer.priceOffer.title)
                .put("priceOffer.state", QPriceOffer.priceOffer.state)
                .put("priceOffer.contact.id", QPriceOffer.priceOffer.contact.id)
                .put("priceOffer.project.id", QPriceOffer.priceOffer.project.id)
                .put("priceOffer.contact.name", QPriceOffer.priceOffer.contact.name)
                .put("priceOffer.start", QPriceOffer.priceOffer.createdDate)
                .put("priceOffer.end", QPriceOffer.priceOffer.createdDate)
                .build();

        if (!rqlFilter.equals("")) {
            predicate = filterParser.parse(rqlFilter, withBuilderAndParam(new QuerydslFilterBuilder(), new QuerydslFilterParam().setMapping(pathMapping)));
        }

        OrderSpecifierList orderSpecifierList = sortParser.parse(sortExpression, QuerydslSortContext.withMapping(pathMapping));

        List<PriceOffer> priceOfferList = this.getPriceOfferPaginate(pageable, rqlFilter, appProfileId, predicate, orderSpecifierList);

        return new CustomPageImpl<>(priceOfferList, pageable, getTotalCount(predicate, appProfileId), getTotalPrice(predicate, appProfileId));
    }

    @Override
    public Optional<PriceOffer> getById(Long id, Long appProfileId) {
        PriceOffer priceOffer = query.selectFrom(QPriceOffer.priceOffer)
                .leftJoin(QPriceOffer.priceOffer.contact).fetchJoin()
                .leftJoin(QPriceOffer.priceOffer.project).fetchJoin()
                .leftJoin(QPriceOffer.priceOffer.packs, QDocumentPack.documentPack).fetchJoin()
                .leftJoin(QPriceOffer.priceOffer.company).fetchJoin()
                .where(QPriceOffer.priceOffer.id.eq(id))
                .orderBy(QDocumentPack.documentPack.position.asc())
                .fetchOne();

        // price offer pack set items
        if (priceOffer != null && !priceOffer.getPacks().isEmpty()) {
            priceOfferPackSetItems(priceOffer);
        }

        return Optional.ofNullable(priceOffer);
    }

    @Override
    public Optional<PriceOffer> getByIdSimple(Long id, Long appProfileId) {
        QPriceOffer qPriceOffer = QPriceOffer.priceOffer;

        return Optional.ofNullable(query.selectFrom(qPriceOffer)
                .where(qPriceOffer.id.eq(id))
                .where(qPriceOffer.appProfile.id.eq(appProfileId))
                .fetchOne());
    }

    @Override
    public PriceOffer getLastPriceOffer(Long companyId, Long appProfileId) {
        return this.query.selectFrom(QPriceOffer.priceOffer)
                .where(QPriceOffer.priceOffer.appProfile.id.eq(appProfileId))
                .where(QPriceOffer.priceOffer.company.id.eq(companyId))
                .orderBy(QPriceOffer.priceOffer.id.desc())
                .limit(1L)
                .fetchOne();
    }

    private void priceOfferPackSetItems(PriceOffer priceOffer) {
        List<DocumentPackItem> priceOfferPackItems = query.selectFrom(QDocumentPackItem.documentPackItem)
                .where(QDocumentPackItem.documentPackItem.pack.id.in(priceOffer.getPacks().stream().map(DocumentPack::getId).collect(Collectors.toList())))
                .leftJoin(QDocumentPackItem.documentPackItem.item, QItem.item).fetchJoin()
                .orderBy(QDocumentPackItem.documentPackItem.position.asc())
                .fetch();

        // price offer pack set items
        priceOffer.getPacks().forEach(documentPack -> documentPack.setPackItems(
                priceOfferPackItems.stream().filter(
                        priceOfferPackItem -> priceOfferPackItem.getPack().getId().equals(documentPack.getId())).collect(Collectors.toList())
        ));
    }

    private Long getTotalCount(Predicate predicate, Long appProfileId) {
        QPriceOffer qPriceOffer = QPriceOffer.priceOffer;

        return query.selectFrom(qPriceOffer)
                .where(predicate)
                .where(QPriceOffer.priceOffer.appProfile.id.eq(appProfileId))
                .fetchCount();
    }

    private BigDecimal getTotalPrice(Predicate predicate, Long appProfileId) {
        return this.query.select(QPriceOffer.priceOffer.priceAfterDiscount.sum())
                .from(QPriceOffer.priceOffer)
                .where(predicate)
                .where(QPriceOffer.priceOffer.appProfile.id.eq(appProfileId))
                .fetchOne();
    }

    //todo tieto spolocne metody dat do abstrakntej triedy pre faktury a cenove ponuky
    private List<PriceOffer> getPriceOfferPaginate(Pageable pageable, String rqlFilter, Long appProfileId, Predicate predicate, OrderSpecifierList orderSpecifierList) {
        JPAQuery<PriceOffer> priceOfferJPAQuery = this.query.selectFrom(QPriceOffer.priceOffer)
                .leftJoin(QPriceOffer.priceOffer.contact).fetchJoin()
                .leftJoin(QPriceOffer.priceOffer.project).fetchJoin()
                .where(predicate)
                .where(QPriceOffer.priceOffer.appProfile.id.eq(appProfileId))
                .orderBy(orderSpecifierList.getOrders().toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        return priceOfferJPAQuery.fetch();
    }
}
