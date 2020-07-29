package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.domain.QDocumentPack;
import com.data.dataxer.models.domain.QDocumentPackItem;
import com.data.dataxer.models.domain.QItem;
import com.data.dataxer.models.domain.QPriceOffer;
import com.querydsl.core.BooleanBuilder;
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
    public Page<PriceOffer> paginate(Pageable pageable, Map<String, String> filter, List<Long> companyIds) {
        QPriceOffer qPriceOffer = QPriceOffer.priceOffer;

        List<PriceOffer> priceOffers = query.selectFrom(qPriceOffer)
                .leftJoin(qPriceOffer.contact).fetchJoin()
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(qPriceOffer.id.desc())
                .where(search(qPriceOffer, filter))
                .fetch();

        return new PageImpl<PriceOffer>(priceOffers, pageable, total());
    }

    // search by condition
    private BooleanBuilder search(QPriceOffer qPriceOffer, Map<String, String> filter) {
        BooleanBuilder where = new BooleanBuilder();

        if (!filter.isEmpty()) {
            if (filter.get("state") != null) {
                where.or(qPriceOffer.state.eq(filter.get("state")));
            }
        }

        return where;
    }

    @Override
    public Optional<PriceOffer> getById(Long id, List<Long> companyIds) {
        QPriceOffer qPriceOffer = QPriceOffer.priceOffer;
        QDocumentPack qDocumentPack = QDocumentPack.documentPack;

        PriceOffer priceOffer = query.selectFrom(qPriceOffer)
                .leftJoin(qPriceOffer.contact).fetchJoin()
                .leftJoin(qPriceOffer.packs, qDocumentPack).fetchJoin()
                .where(qPriceOffer.id.eq(id))
                .orderBy(qDocumentPack.position.asc())
                .fetchOne();

        // price offer pack set items
        if (priceOffer != null) {
            priceOfferPackSetItems(priceOffer);
        }

        return Optional.ofNullable(priceOffer);
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

    @Override
    public Optional<PriceOffer> getByIdSimple(Long id, List<Long> companyIds) {
        QPriceOffer qPriceOffer = QPriceOffer.priceOffer;

        return Optional.ofNullable(query.selectFrom(qPriceOffer)
                .where(qPriceOffer.id.eq(id))
                .where(qPriceOffer.company.id.in(companyIds))
                .fetchOne());
    }
}
