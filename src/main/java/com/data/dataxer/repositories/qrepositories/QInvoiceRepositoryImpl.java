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
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.vineey.rql.filter.FilterContext.withBuilderAndParam;

@Repository
public class QInvoiceRepositoryImpl implements QInvoiceRepository {

    private final JPAQueryFactory query;

    public QInvoiceRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Invoice> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long companyId) {
        DefaultSortParser sortParser = new DefaultSortParser();
        DefaultFilterParser filterParser = new DefaultFilterParser();

        Predicate predicate = new BooleanBuilder();

        Map<String, Path> pathMapping = ImmutableMap.<String, Path>builder()
                .put("invoice.id", QInvoice.invoice.id)
                .put("invoice.company.id", QInvoice.invoice.company.id)
                .put("invoice.title", QInvoice.invoice.title)
                .put("invoice.state", QInvoice.invoice.state)
                .put("invoice.document_type", QInvoice.invoice.documentType)
                .put("invoice.contact.id", QInvoice.invoice.contact.id)
                .put("invoice.contact.name", QInvoice.invoice.contact.name)
                .put("invoice.project.id", QInvoice.invoice.project.id)
                .put("invoice.documentType", QInvoice.invoice.documentType)
                .put("invoice.start", QInvoice.invoice.createdDate)
                .put("invoice.end", QInvoice.invoice.createdDate)
                .build();

        if (!rqlFilter.equals("")) {
            predicate = filterParser.parse(rqlFilter, withBuilderAndParam(new QuerydslFilterBuilder(), new QuerydslFilterParam().setMapping(pathMapping)));
        }

        OrderSpecifierList orderSpecifierList = sortParser.parse(sortExpression, QuerydslSortContext.withMapping(pathMapping));

        List<Invoice> invoiceList = this.getInvoicePaginates(pageable, rqlFilter, companyId, predicate, orderSpecifierList);

        return new PageImpl<>(invoiceList, pageable, getTotalCount(predicate));
    }

    @Override
    public Optional<Invoice> getById(Long id, List<Long> companyIds) {

        Invoice invoice = query.selectFrom(QInvoice.invoice)
                .leftJoin(QInvoice.invoice.contact).fetchJoin()
                .leftJoin(QInvoice.invoice.project).fetchJoin()
                .leftJoin(QInvoice.invoice.packs, QDocumentPack.documentPack).fetchJoin()
                .where(QInvoice.invoice.id.eq(id))
                .where(QInvoice.invoice.company.id.in(companyIds))
                .orderBy(QDocumentPack.documentPack.position.asc())
                .fetchOne();

        if (invoice != null) {
            this.invoicePackSetItems(invoice);
        }
        return Optional.ofNullable(invoice);
    }

    @Override
    public Optional<Invoice> getById(Long id) {
        QInvoice qInvoice = QInvoice.invoice;
        QDocumentPack qDocumentPack = QDocumentPack.documentPack;

        Invoice invoice = query.selectFrom(qInvoice)
                .leftJoin(qInvoice.contact).fetchJoin()
                .leftJoin(qInvoice.company).fetchJoin()
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
                .where(QInvoice.invoice.company.id.in(companyIds))
                .fetchOne());
    }

    @Override
    public List<Invoice> getAllInvoicesIdInAndType(List<Long> ids, DocumentType type, List<Long> companyIds) {
        return this.query.selectFrom(QInvoice.invoice)
                .leftJoin(QInvoice.invoice.contact).fetchJoin()
                .leftJoin(QInvoice.invoice.project).fetchJoin()
                .leftJoin(QInvoice.invoice.packs, QDocumentPack.documentPack).fetchJoin()
                .where(QInvoice.invoice.id.in(ids))
                .where(QInvoice.invoice.documentType.eq(type))
                .where(QInvoice.invoice.company.id.in(companyIds))
                .fetch();
    }

    private long getTotalCount(Predicate predicate) {
        QInvoice qInvoice = QInvoice.invoice;

        return this.query.selectFrom(qInvoice)
                .where(predicate)
                .fetchCount();
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

    //todo tieto spolocne metody dat do abstrakntej triedy pre faktury a cenove ponuky
    private List<Invoice> getInvoicePaginates(Pageable pageable, String rqlFilter, Long companyId, Predicate predicate, OrderSpecifierList orderSpecifierList) {
        JPAQuery<Invoice> invoiceJPAQuery = this.query.selectFrom(QInvoice.invoice)
                .leftJoin(QInvoice.invoice.contact).fetchJoin()
                .leftJoin(QInvoice.invoice.project).fetchJoin()
                .where(predicate)
                .where(QInvoice.invoice.company.id.eq(companyId))
                .orderBy(orderSpecifierList.getOrders().toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (!rqlFilter.contains("invoice.company.id")) { // todo make refakt
            invoiceJPAQuery.where(QInvoice.invoice.company.id.eq(companyId));
        }

        return invoiceJPAQuery.fetch();
    }
}
