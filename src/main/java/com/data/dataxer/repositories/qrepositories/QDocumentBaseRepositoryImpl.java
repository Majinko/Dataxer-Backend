package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.DocumentBase;
import com.data.dataxer.models.domain.QDocumentBase;
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
    public List<DocumentBase> getAllDocumentByIds(List<Long> documentIds, Long companyId) {
        for (Long id:documentIds
             ) {
            System.out.println("Id:" + id);
        }
        return this.query.selectFrom(QDocumentBase.documentBase)
                .where(QDocumentBase.documentBase.id.in(documentIds))
                .where(QDocumentBase.documentBase.company.id.eq(companyId))
                .fetch();

    }

}
