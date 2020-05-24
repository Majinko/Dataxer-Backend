package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class QItemRepositoryImpl implements QItemRepository {
    private final JPAQueryFactory query;

    public QItemRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Item getById(long id,  List<Long> companyIds) {
        QItem qItem = QItem.item;
        QItemPrice qItemPrice = QItemPrice.itemPrice;
        QCategory qCategory = QCategory.category;
        QContact qContact = QContact.contact;

        return query
                .selectFrom(qItem)
                .where(qItem.company.id.in(companyIds))
                .where(qItem.id.eq(id))
                .leftJoin(qItem.itemPrices, qItemPrice).fetchJoin()
                .leftJoin(qItem.category, qCategory).fetchJoin()
                .leftJoin(qItem.supplier, qContact).fetchJoin()
                .fetchOne();
    }
}
