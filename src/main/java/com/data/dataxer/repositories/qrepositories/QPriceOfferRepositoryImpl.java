package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.*;
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
        QPriceOfferPack qPriceOfferPack = QPriceOfferPack.priceOfferPack;

        PriceOffer priceOffer = query.selectFrom(qPriceOffer)
                .leftJoin(qPriceOffer.contact).fetchJoin()
                .leftJoin(qPriceOffer.packs, qPriceOfferPack).fetchJoin()
                .where(qPriceOffer.id.eq(id))
                .orderBy(qPriceOfferPack.position.asc())
                .fetchOne();

        // price offer pack set items
        if (priceOffer != null)
            priceOfferPackSetItems(priceOffer);

        return Optional.ofNullable(priceOffer);
    }

    // set price offer pack and pack items
    private void priceOfferPackSetItems(PriceOffer priceOffer) {
        QPriceOfferPackItem qPriceOfferPackItem = QPriceOfferPackItem.priceOfferPackItem;
        QItem qItem = QItem.item;

        // get all pack items by pack id in
        List<PriceOfferPackItem> priceOfferPackItems = query.selectFrom(qPriceOfferPackItem)
                .where(QPriceOfferPackItem.priceOfferPackItem.priceOfferPack.id.in(priceOffer.getPacks().stream().map(PriceOfferPack::getId).collect(Collectors.toList())))
                .leftJoin(qPriceOfferPackItem.item, qItem).fetchJoin()
                .orderBy(qPriceOfferPackItem.position.asc())
                .fetch();


        // price offer pack set items
        priceOffer.getPacks().forEach(priceOfferPack -> {
            priceOfferPack.setItems(
                    priceOfferPackItems.stream().filter(
                            priceOfferPackItem -> priceOfferPackItem.getPriceOfferPack().getId().equals(priceOfferPack.getId())).collect(Collectors.toList())
            );
        });
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
