package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.domain.QDocumentPack;
import com.data.dataxer.models.domain.QDocumentPackItem;
import com.data.dataxer.models.domain.QInvoice;
import com.data.dataxer.models.domain.QItem;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
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
    public Page<Invoice> paginate(Pageable pageable, Filter filter, List<Long> companyIds) {
        QInvoice qInvoice = QInvoice.invoice;
        BooleanBuilder filterCondition = new BooleanBuilder();

        if (filter != null && !filter.isEmpty()) {
            filterCondition = filter.buildInvoiceFilterPredicate();
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
}
