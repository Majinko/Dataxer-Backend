package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class QItemRepositoryImpl implements QItemRepository {
    private final JPAQueryFactory query;

    public QItemRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Item getById(long id, List<Long> companyIds) {
        QItem qItem = QItem.item;
        QItemPrice qItemPrice = QItemPrice.itemPrice;
        QCategory qCategory = QCategory.category;
        QContact qContact = QContact.contact;

        Item item = query
                .selectFrom(qItem)
                .where(qItem.company.id.in(companyIds))
                .where(qItem.id.eq(id))
                .leftJoin(qItem.itemPrices, qItemPrice).fetchJoin()
                .leftJoin(qItem.category, qCategory).fetchJoin()
                .leftJoin(qItem.supplier, qContact).fetchJoin()
                .fetchOne();

        if (item != null) {
            itemSetStorage(item);
        }

        return item;
    }

    @Override
    public Optional<List<Item>> findAllByTitleContainsAndCompanyIdIn(String q, List<Long> companyIds) {
        QItem qItem = QItem.item;

        return Optional.ofNullable(query
                .selectFrom(qItem)
                .where(qItem.company.id.in(companyIds))
                .where(qItem.title.containsIgnoreCase(q))
                .leftJoin(qItem.itemPrices).fetchJoin()
                .fetch());
    }

    @Override
    public Page<Item> paginate(Pageable pageable, List<Long> companyIds) {
        List<Item> items = query.selectFrom(QItem.item)
                .where(QItem.item.company.id.in(companyIds))
                .leftJoin(QItem.item.storage, QStorage.storage).fetchJoin()
                .distinct()
                .fetch();

        return new PageImpl<Item>(items, pageable, total(companyIds));
    }

    private long total(List<Long> companyIds) {
        return query.selectFrom(QItem.item)
                .where(QItem.item.company.id.in(companyIds)).fetchCount();
    }

    private void itemSetStorage(Item item) {
        item.setStorage(
                query.selectFrom(QStorage.storage)
                        .where(QStorage.storage.fileAbleId.eq(item.getId()))
                        .where(QStorage.storage.fileAbleType.eq("item"))
                        .fetch()
        );
    }
}
