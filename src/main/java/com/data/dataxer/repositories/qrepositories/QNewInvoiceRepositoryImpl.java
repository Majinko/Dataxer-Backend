package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.NewInvoice;
import com.data.dataxer.models.domain.QNewInvoice;
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
public class QNewInvoiceRepositoryImpl implements QNewInvoiceRepository {

    private final JPAQueryFactory query;

    public QNewInvoiceRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<NewInvoice> getById(Long id, List<Long> companyIds) {
        return Optional.empty();
    }

    @Override
    public Optional<NewInvoice> getByIdSimple(Long id, List<Long> companyIds) {
        return Optional.empty();
    }

    @Override
    public Page<NewInvoice> paginate(Pageable pageable, Filter[] filters, List<Long> companyIds) {
        QNewInvoice qNewInvoice = QNewInvoice.newInvoice;
        BooleanBuilder filterCondition = new BooleanBuilder();

        if (filters.length > 0) {
            for (Filter filter : filters) {
                filterCondition = filter.buildInvoiceFilterPredicate();
            }
        }

        List<NewInvoice> newInvoices = this.query.selectFrom(qNewInvoice)
                .leftJoin(qNewInvoice.contact).fetchJoin()
                .where(filterCondition)
                .where(qNewInvoice.company.id.in(companyIds))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(qNewInvoice.id.desc())
                .fetch();

        return new PageImpl<>(newInvoices, pageable, total());
    }

    private Long total() {
        QNewInvoice qNewInvoice = QNewInvoice.newInvoice;
        return this.query.selectFrom(qNewInvoice).fetchCount();
    }
}
