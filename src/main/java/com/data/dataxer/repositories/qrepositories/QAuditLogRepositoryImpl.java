package com.data.dataxer.repositories.qrepositories;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class QAuditLogRepositoryImpl implements QAuditLogRepository {

    private final JPAQueryFactory query;

    QAuditLogRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

}
