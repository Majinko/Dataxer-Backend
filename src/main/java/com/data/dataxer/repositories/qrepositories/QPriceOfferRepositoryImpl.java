package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.domain.QDocument;
import com.data.dataxer.models.domain.QDocumentPackItem;
import com.data.dataxer.models.domain.QPriceOffer;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class QPriceOfferRepositoryImpl implements QPriceOfferRepository {
    private final JPAQueryFactory query;

    public QPriceOfferRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    private Long total() {
        QPriceOffer qPriceOffer = QPriceOffer.priceOffer;

        return query.selectFrom(qPriceOffer).fetchCount();
    }

    @Override
    public Page<PriceOffer> paginate(Pageable pageable, List<Long> companyIds) {
        QPriceOffer qPriceOffer = QPriceOffer.priceOffer;

        List<PriceOffer> priceOffers = query.selectFrom(qPriceOffer)
                .leftJoin(qPriceOffer.contact).fetchJoin()
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(qPriceOffer.id.desc())
                .fetch();

        return new PageImpl<PriceOffer>(priceOffers, pageable, total());
    }

    @Override
    public Optional<PriceOffer> getById(Long id, List<Long> companyIds) {
        QPriceOffer qPriceOffer = QPriceOffer.priceOffer;
        QDocument qDocument = QDocument.document;

        PriceOffer priceOffer = query.selectFrom(qPriceOffer)
                .leftJoin(qPriceOffer.contact).fetchJoin()
                .leftJoin(qDocument.documentPack).on(qPriceOffer.id.eq(qDocument.documentId))
                .where(qPriceOffer.id.eq(id))
                .orderBy(qDocument.documentPack.position.asc())
                .fetchOne();

        // price offer pack set items
        if (priceOffer != null){
            priceOfferPackSetItems(priceOffer);
        }

        return Optional.ofNullable(priceOffer);
    }

    //WIP
    private void priceOfferPackSetItems(PriceOffer priceOffer) {
        QDocumentPackItem qDocumentPackItem = QDocumentPackItem.documentPackItem;

        List<DocumentPackItem> documentPackItems = query.selectFrom(qDocumentPackItem)
                .fetch();

    }

    @Override
    public Optional<PriceOffer> getByIdSimple(Long id, List<Long> companyIds) {
        QPriceOffer qPriceOffer = QPriceOffer.priceOffer;

        return Optional.ofNullable(query.selectFrom(qPriceOffer)
                .where(qPriceOffer.id.eq(id))
                .where(qPriceOffer.company.id.in(companyIds))
                .fetchOne());
    }
}