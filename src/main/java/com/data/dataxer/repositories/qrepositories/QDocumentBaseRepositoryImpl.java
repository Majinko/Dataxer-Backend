package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.DocumentBase;
import com.data.dataxer.models.domain.QDocumentBase;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class QDocumentBaseRepositoryImpl implements QDocumentBaseRepository {

    private final JPAQueryFactory query;

    public QDocumentBaseRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<DocumentBase> getAllDocumentByIds(List<Long> documentIds, Long defaultProfileId) {
        return this.query.selectFrom(QDocumentBase.documentBase)
                .where(QDocumentBase.documentBase.id.in(documentIds))
                .where(QDocumentBase.documentBase.appProfile.id.eq(defaultProfileId))
                .fetch();

    }

    @Override
    public List<DocumentBase> getAllByQueryString(Long documentId, String search, Long defaultProfileId) {
        BooleanBuilder where = new BooleanBuilder();

        if (!search.isEmpty()) {
            where.and(QDocumentBase.documentBase.title.containsIgnoreCase(search));
        }

        return this.query.selectFrom(QDocumentBase.documentBase)
                .where(where)
                .where(QDocumentBase.documentBase.id.notIn(documentId))
                .where(QDocumentBase.documentBase.appProfile.id.eq(defaultProfileId))
                .orderBy(QDocumentBase.documentBase.id.desc())
                .limit(15L)
                .fetch();
    }
}
