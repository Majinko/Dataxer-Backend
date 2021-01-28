package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Demand;
import com.data.dataxer.models.domain.QDemand;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class QDemandRepositoryImpl implements QDemandRepository {
    private final JPAQueryFactory query;

    public QDemandRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    private Long total( List<Long> companyIds) {
        QDemand qDemand = QDemand.demand;

        return query.selectFrom(qDemand).where(qDemand.company.id.in(companyIds)).fetchCount();
    }

    @Override
    public Page<Demand> paginate(Pageable pageable, List<Long> companyIds) {
        QDemand qDemand = QDemand.demand;

        List<Demand> demandList = query
                .selectFrom(qDemand)
                .leftJoin(qDemand.category).fetchJoin()
                .leftJoin(qDemand.contact).fetchJoin()
                .where(qDemand.company.id.in(companyIds))
                .fetch();

        return new PageImpl<Demand>(demandList, pageable, total(companyIds));
    }

    @Override
    public Optional<Demand> getById(Long id, List<Long> companyIds) {
        QDemand qDemand = QDemand.demand;

        return Optional.ofNullable(query.selectFrom(qDemand)
                .where(qDemand.company.id.in(companyIds))
                .where(qDemand.id.eq(id))
                .leftJoin(qDemand.category).fetchJoin()
                .leftJoin(qDemand.contact).fetchJoin()
                .fetchOne());
    }
}
