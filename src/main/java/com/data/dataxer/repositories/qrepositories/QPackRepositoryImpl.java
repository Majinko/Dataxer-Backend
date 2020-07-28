package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class QPackRepositoryImpl implements QPackRepository {
    private final JPAQueryFactory query;

    public QPackRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Pack> paginate(Pageable pageable, List<Long> companyIds) {
        QPack qPack = QPack.pack;

        List<Pack> packs = query.selectFrom(qPack)
                .where(qPack.company.id.in(companyIds))
                .fetch();

        return new PageImpl<>(packs, pageable, packs.size());
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
                .leftJoin(qPack.packItems, qPackItem).fetchJoin()
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
}
