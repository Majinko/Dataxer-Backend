package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.DocumentRelations;
import com.data.dataxer.models.domain.QDocumentRelations;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class QDocumentRelationsRepositoryImpl implements QDocumentRelationsRepository {

    private final JPAQueryFactory query;
    private final EntityManager entityManager;

    public QDocumentRelationsRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager.getEntityManagerFactory().createEntityManager();
        this.query = new JPAQueryFactory(this.entityManager);
    }

    @Override
    public Optional<DocumentRelations> getById(Long id, Long companyId, Boolean disableFilter) {
        QDocumentRelations qDocumentRelations = QDocumentRelations.documentRelations;

        if (!disableFilter) {
            this.entityManager.unwrap(Session.class).enableFilter("companyCondition").setParameter("companyId", companyId);
        }

        return Optional.ofNullable(this.query.selectFrom(qDocumentRelations)
                .where(qDocumentRelations.id.eq(id))
                .fetchOne());
    }
}
