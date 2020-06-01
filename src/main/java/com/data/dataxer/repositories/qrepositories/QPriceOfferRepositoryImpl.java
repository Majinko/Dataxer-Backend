package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.PriceOffer;
import com.data.dataxer.models.domain.QPriceOffer;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class QPriceOfferRepositoryImpl implements QPriceOfferRepository {
    private final JPAQueryFactory query;

    public QPriceOfferRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<PriceOffer> paginate(Pageable pageable, List<Long> companyIds) {
        QPriceOffer qPriceOffer = QPriceOffer.priceOffer;

        List<PriceOffer> priceOffers = query.selectFrom(qPriceOffer)
                .join(qPriceOffer.contact).fetchJoin()
                .fetch();

        return new PageImpl<PriceOffer>(priceOffers, pageable, priceOffers.size());
    }

    @Override
    public Optional<PriceOffer> getById(Long id, List<Long> companyIds) {
        QPriceOffer qPriceOffer = QPriceOffer.priceOffer;

        return Optional.ofNullable(query.selectFrom(qPriceOffer)
                .join(qPriceOffer.contact).fetchJoin()
                .where(qPriceOffer.id.eq(id))
                .where(qPriceOffer.company.id.in(companyIds))
                .fetchOne());
    }
}
