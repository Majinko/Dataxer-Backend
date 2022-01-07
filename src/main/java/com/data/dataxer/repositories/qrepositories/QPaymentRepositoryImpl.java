package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.enums.DocumentType;
import com.github.vineey.rql.filter.parser.DefaultFilterParser;
import com.github.vineey.rql.querydsl.filter.QuerydslFilterBuilder;
import com.github.vineey.rql.querydsl.filter.QuerydslFilterParam;
import com.github.vineey.rql.querydsl.sort.OrderSpecifierList;
import com.github.vineey.rql.querydsl.sort.QuerydslSortContext;
import com.github.vineey.rql.sort.parser.DefaultSortParser;
import com.google.common.collect.ImmutableMap;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.vineey.rql.filter.FilterContext.withBuilderAndParam;

@Repository
public class QPaymentRepositoryImpl implements QPaymentRepository {

    private final JPAQueryFactory query;

    public QPaymentRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Payment> paginate(Pageable pageable, String rqlFilter, String sortExpression, List<Long> companyIds) {
        DefaultSortParser sortParser = new DefaultSortParser();
        DefaultFilterParser filterParser = new DefaultFilterParser();
        Predicate predicate = new BooleanBuilder();

        QPayment qPayment = QPayment.payment;

        Map<String, Path> pathMapping = ImmutableMap.<String, Path>builder()
                .put("payment.id", QPayment.payment.id)
                .build();

        if (!rqlFilter.equals("")) {
            predicate = filterParser.parse(rqlFilter, withBuilderAndParam(new QuerydslFilterBuilder(), new QuerydslFilterParam()
                    .setMapping(pathMapping)));
        }
        OrderSpecifierList orderSpecifierList = sortParser.parse(sortExpression, QuerydslSortContext.withMapping(pathMapping));

        List<Payment> paymentList = this.query.selectFrom(qPayment)
                .where(predicate)
                .where(qPayment.company.id.in(companyIds))
                .orderBy(orderSpecifierList.getOrders().toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(paymentList, pageable, getTotalCount(predicate));
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

    @Override
    public BigDecimal getDocumentTotalPrice(Long documentId, DocumentType documentType) {
        switch (documentType) {
            case COST:
                Cost cost = this.query.selectFrom(QCost.cost)
                        .where(QCost.cost.id.eq(documentId))
                        .fetchOne();

                return cost != null ? cost.getTotalPrice() : null;
            case INVOICE:
            case PROFORMA:
            case SUMMARY_INVOICE:
            case TAX_DOCUMENT:
                QInvoice qInvoice = QInvoice.invoice;

                Invoice invoice = this.query.selectFrom(qInvoice)
                        .where(qInvoice.id.eq(documentId))
                        .fetchOne();

                if (invoice != null) {
                    return invoice.getTotalPrice().subtract(invoice.getDiscount() != null ? invoice.countDiscountTotalPrice() : new BigDecimal(0));
                } else {
                    throw new RuntimeException("Invoice with id" + documentId + "not found");
                }

            case PRICE_OFFER:
            default:
                QPriceOffer qPriceOffer = QPriceOffer.priceOffer;

                PriceOffer priceOffer = this.query.selectFrom(qPriceOffer)
                        .where(qPriceOffer.id.eq(documentId))
                        .fetchOne();
                if (priceOffer != null) {
                    return priceOffer.getTotalPrice();
                } else {
                    throw new RuntimeException("Price offer with id" + documentId + "not found");
                }
        }
    }

    @Override
    public BigDecimal getPayedTotalPrice(Long documentId, DocumentType documentType) {
        QPayment qPayment = QPayment.payment;

        List<Payment> payments = this.query
                .selectFrom(qPayment)
                .where(qPayment.documentId.eq(documentId))
                .where(qPayment.documentType.eq(documentType))
                .orderBy(qPayment.id.desc())
                .fetch();

        BigDecimal payedTotalPrice = BigDecimal.valueOf(0);

        for (Payment payment : payments) {
            payedTotalPrice = payedTotalPrice.add(payment.getPayedValue());
        }
        return payedTotalPrice;
    }

    @Override
    public List<Payment> getPaymentsByDocumentIdSortedByPayDate(Long documentId, List<Long> companyIds) {
        QPayment qPayment = QPayment.payment;

        return this.query.selectFrom(qPayment)
                .where(qPayment.documentId.eq(documentId))
                .where(qPayment.company.id.in(companyIds))
                .orderBy(qPayment.payedDate.desc())
                .fetch();

    }

    @Override
    public Optional<Payment> getNewestByDocumentId(Long documentId, List<Long> companyIds) {
        QPayment qPayment = QPayment.payment;

        return Optional.ofNullable(this.query.selectFrom(qPayment)
                .where(qPayment.documentId.eq(documentId))
                .where(qPayment.company.id.in(companyIds))
                .orderBy(qPayment.createdAt.desc())
                .fetchFirst());
    }


    private long getTotalCount(Predicate predicate) {
        QPayment qPayment = QPayment.payment;

        return this.query.selectFrom(qPayment)
                .where(predicate)
                .fetchCount();
    }
}
