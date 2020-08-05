package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.filters.SearchableDates;
import com.data.dataxer.filters.SearchableDecimals;
import com.data.dataxer.filters.SearchableStrings;
import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.enums.DocumentState;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class QInvoiceRepositoryImpl implements QInvoiceRepository {

    private final JPAQueryFactory query;

    public QInvoiceRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Invoice> paginate(Pageable pageable, String filter, List<Long> companyIds) {
        QInvoice qInvoice = QInvoice.invoice;
        BooleanBuilder filterCondition = new BooleanBuilder();

        if (!filter.isEmpty()) {
            Filter invoicesFilter = new Filter(filter);
            filterCondition = this.buildFilterPredicate(invoicesFilter);
        }


        List<Invoice> invoices = query.selectFrom(qInvoice)
                .leftJoin(qInvoice.contact).fetchJoin()
                .where(filterCondition)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(qInvoice.id.desc())
                .fetch();

        return new PageImpl<Invoice>(invoices, pageable, total());
    }

    @Override
    public Optional<Invoice> getById(Long id, List<Long> companyIds) {
        QInvoice qInvoice = QInvoice.invoice;
        QDocumentPack qDocumentPack = QDocumentPack.documentPack;

        Invoice invoice = query.selectFrom(qInvoice)
                .leftJoin(qInvoice.contact).fetchJoin()
                .leftJoin(qInvoice.packs, qDocumentPack).fetchJoin()
                .where(qInvoice.id.eq(id))
                .orderBy(qDocumentPack.position.asc())
                .fetchOne();

        if (invoice != null) {
            this.invoicePackSetItems(invoice);
        }
        return Optional.ofNullable(invoice);
    }

    @Override
    public Optional<Invoice> getByIdSimple(Long id, List<Long> companyIds) {
        QInvoice qInvoice = QInvoice.invoice;

        return Optional.ofNullable(query.selectFrom(qInvoice)
                .where(qInvoice.id.eq(id))
                .where(qInvoice.company.id.in(companyIds))
                .fetchOne());
    }

    @Override
    public Page<Invoice> getByClient(Pageable pageable, Long contactId, List<Long> companyIds) {
        QInvoice qInvoice = QInvoice.invoice;

        List<Invoice> invoices = query.selectFrom(qInvoice)
                .leftJoin(qInvoice.contact).fetchJoin()
                .where(qInvoice.contact.id.eq(contactId))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(qInvoice.id.desc())
                .fetch();
        return new PageImpl<Invoice>(invoices, pageable, total());
    }

    private Long total() {
        QInvoice qInvoice = QInvoice.invoice;

        return query.selectFrom(qInvoice).fetchCount();
    }

     private void invoicePackSetItems(Invoice invoice) {
         QDocumentPackItem qDocumentPackItem = QDocumentPackItem.documentPackItem;
         QItem qItem = QItem.item;

         List<DocumentPackItem> invoicePackItems = query.selectFrom(qDocumentPackItem)
                 .where(qDocumentPackItem.pack.id.in(invoice.getPacks().stream().map(DocumentPack::getId).collect(Collectors.toList())))
                 .leftJoin(qDocumentPackItem.item, qItem).fetchJoin()
                 .orderBy(qDocumentPackItem.position.asc())
                 .fetch();
         invoice.getPacks().forEach(documentPack -> documentPack.setPackItems(
                 invoicePackItems.stream().filter(
                         invoicePackItem -> invoicePackItem.getPack().getId().equals(documentPack.getId())).collect(Collectors.toList())
         ));
    }

    //ak funguje presunut do Filter classy -> zglobalizovanie
    //Mozny TO-DO: poriesit co ak je tam nieco co nie je searchable (nepadne do ziadnej vetvy)
    private BooleanBuilder buildFilterPredicate(Filter filter) throws RuntimeException {
        Path<Invoice> invoice = Expressions.path(Invoice.class, "invoice");
        BooleanBuilder builder = new BooleanBuilder();

        if (SearchableStrings.isSearchableString(filter.getColumnId())) {
            Path<String> stringBase = Expressions.path(String.class, invoice, filter.getColumnId());
            Expression<String> value = Expressions.constant(filter.getValues().get(0));
            return builder.or(Expressions.predicate(filter.resolveOperator(), stringBase, value));
        }
        if (SearchableDecimals.isSearchableString(filter.getColumnId())) {
            Path<BigDecimal> bigDecimalBase = Expressions.path(BigDecimal.class, invoice, filter.getColumnId());
            Expression<BigDecimal> value = Expressions.constant(new BigDecimal(filter.getValues().get(0)));
            if (filter.getValues().size() > 1) {
                Expression<BigDecimal> value2 = Expressions.constant(new BigDecimal(filter.getValues().get(1)));
                return builder.or(Expressions.predicate(filter.resolveOperator(), bigDecimalBase, value, value2));
            }
            return builder.or(Expressions.predicate(filter.resolveOperator(), bigDecimalBase, value));
        }
        if (SearchableDates.isSearchableString(filter.getColumnId())) {
            Path<LocalDate> dateBase = Expressions.path(LocalDate.class, invoice, filter.getColumnId());
            Expression<LocalDate> value = Expressions.constant(LocalDate.parse(filter.getValues().get(0)));
            if (filter.getValues().size() > 1) {
                Expression<LocalDate> value2 = Expressions.constant(LocalDate.parse(filter.getValues().get(1)));
                return builder.or(Expressions.predicate(filter.resolveOperator(), dateBase, value, value2));
            }
            return builder.or(Expressions.predicate(filter.resolveOperator(), dateBase, value));
        }
        if (filter.getColumnId().equals("state")) {
            Path<DocumentState.InvoiceStates> stateBase = Expressions.path(DocumentState.InvoiceStates.class, invoice, filter.getColumnId());
            if (filter.getValues().size() > 1) {
                for (String state : filter.getValues()) {
                    Expression<DocumentState.InvoiceStates> value = Expressions.constant(DocumentState.InvoiceStates.getStateByCode(state));
                    builder.or(Expressions.predicate(Ops.EQ, stateBase, value));
                }
                return builder;
            }
            Expression<DocumentState.InvoiceStates> value = Expressions.constant(DocumentState.InvoiceStates.getStateByCode(filter.getValues().get(0)));
            return builder.or(Expressions.predicate(filter.resolveOperator(), stateBase, value));
        }
        throw new RuntimeException("Not valid filter!");
    }
}
