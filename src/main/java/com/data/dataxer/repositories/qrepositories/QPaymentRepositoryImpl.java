package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.Payment;
import com.data.dataxer.models.domain.QPayment;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class QPaymentRepositoryImpl implements QPaymentRepository {

    private final JPAQueryFactory query;

    public QPaymentRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Payment> paginate(Pageable pageable, Filter filter, List<Long> companyIds) {
        QPayment qPayment = QPayment.payment;
        BooleanBuilder filterCondition = new BooleanBuilder();

        //will be implemented later
        /*if (!filter.isEmpty()) {
            filterCondition = filter.buildPaymentFilterPredicate();
        }*/
        List<Payment> payments = this.query
                .selectFrom(qPayment)
                .where(qPayment.company.id.in(companyIds))
                .where(filterCondition)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(qPayment.id.desc())
                .fetch();

        return new PageImpl<Payment>(payments, pageable, total());
    }

    @Override
    public Optional<Payment> getById(Long id, List<Long> companyIds) {
        QPayment qPayment = QPayment.payment;

        return Optional.ofNullable(this.query
                .selectFrom(qPayment)
                .where(qPayment.company.id.in(companyIds))
                .where(qPayment.id.eq(id))
                .orderBy(qPayment.id.desc())
                .fetchOne());
    }

    private long total() {
        QPayment qPayment = QPayment.payment;
        return this.query.selectFrom(qPayment).fetchCount();
    }
}
