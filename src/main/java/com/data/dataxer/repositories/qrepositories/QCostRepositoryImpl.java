package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Cost;
import com.data.dataxer.models.domain.QCost;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class QCostRepositoryImpl implements QCostRepository {

    private final JPAQueryFactory query;

    public QCostRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Cost> paginate(Pageable pageable, List<Long> companyIds) {
        QCost qCost = QCost.cost;

        QueryResults<Cost> costResults = this.query.selectFrom(qCost)
                .leftJoin(qCost.contact).fetchJoin()
                .leftJoin(qCost.project).fetchJoin()
                .leftJoin(qCost.category).fetchJoin()
                .where(qCost.company.id.in(companyIds))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(qCost.id.desc())
                .fetchResults();

        return new PageImpl<>(costResults.getResults(), pageable, costResults.getTotal());
    }

    @Override
    public Optional<Cost> getById(Long id, List<Long> companyIds) {
        return Optional.ofNullable(
                this.constructGetAllByIdAndCompanyIds(id, companyIds).fetchOne()
        );
    }

    @Override
    public Cost getByIdWithRelation(Long id, List<Long> companyIds) {
        return this.constructGetAllByIdAndCompanyIds(id, companyIds)
                .leftJoin(QCost.cost.category).fetchJoin()
                .leftJoin(QCost.cost.contact).fetchJoin()
                .leftJoin(QCost.cost.project).fetchJoin()
                .leftJoin(QCost.cost.files).fetchJoin()
                .fetchOne();
    }

    private JPAQuery<Cost> constructGetAllByIdAndCompanyIds(Long id, List<Long> companyIds) {
        return query.selectFrom(QCost.cost)
                .where(QCost.cost.company.id.in(companyIds))
                .where(QCost.cost.id.eq(id));
    }
}
