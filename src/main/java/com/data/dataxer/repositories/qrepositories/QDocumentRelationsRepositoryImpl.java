package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.DocumentRelations;
import com.data.dataxer.models.domain.QDocumentRelations;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class QDocumentRelationsRepositoryImpl implements QDocumentRelationsRepository {

    private final JPAQueryFactory query;

    public QDocumentRelationsRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<DocumentRelations> getById(Long id, Long companyId) {
        QDocumentRelations qDocumentRelations = QDocumentRelations.documentRelations;

        return Optional.ofNullable(this.query.selectFrom(qDocumentRelations)
                .where(qDocumentRelations.id.eq(id))
                .where(qDocumentRelations.company.id.eq(companyId))
                .fetchOne());
    }

    @Override
    public List<DocumentRelations> getAllRelatedByOriginalId(Long originalDocumentId, Long companyId) {
        return this.query.selectFrom(QDocumentRelations.documentRelations)
                .where(QDocumentRelations.documentRelations.documentId.eq(originalDocumentId))
                .fetch();
    }


}
