package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.domain.QInvoice;
import com.data.dataxer.models.domain.QItem;
import com.data.dataxer.models.page.CustomPageImpl;
import com.data.dataxer.models.enums.DocumentType;
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
import io.github.perplexhub.rsql.RSQLQueryDslSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class QInvoiceRepositoryImpl implements QInvoiceRepository {
    private final JPAQueryFactory query;

    public QInvoiceRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Invoice> paginate(Pageable pageable, String rqlFilter, String sortExpression, List<Long> companyIds) {
        Predicate predicate = buildPredicate(rqlFilter);
        OrderSpecifierList orderSpecifierList = buildSort(sortExpression);

        List<Long> invoiceIds = this.getInvoiceIdsPaginate(pageable, rqlFilter, companyIds, predicate, orderSpecifierList);

        List<Invoice> invoices = this.query.selectFrom(QInvoice.invoice)
                .leftJoin(QInvoice.invoice.contact).fetchJoin()
                .leftJoin(QInvoice.invoice.project).fetchJoin()
                .leftJoin(QInvoice.invoice.payments).fetchJoin()
                .where(QInvoice.invoice.id.in(invoiceIds))
                .distinct()
                .fetch();

        return new CustomPageImpl<>(invoices, pageable, getTotalCount(predicate), getTotalPrice(predicate));
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
        return this.query.selectDistinct(QInvoice.invoice)
                .from(QInvoice.invoice)
                .leftJoin(QInvoice.invoice.contact).fetchJoin()
                .leftJoin(QInvoice.invoice.project).fetchJoin()
                .leftJoin(QInvoice.invoice.packs, QDocumentPack.documentPack).fetchJoin()
                .where(QInvoice.invoice.id.in(ids))
                .where(QInvoice.invoice.documentType.eq(type))
                .where(QInvoice.invoice.company.id.in(companyIds))
                .fetch();
    }

    @Override
    public Invoice getLastInvoice(DocumentType type, Long companyId) {
        return this.query.selectFrom(QInvoice.invoice)
                .where(QInvoice.invoice.company.id.eq(companyId))
                .where(QInvoice.invoice.documentType.eq(type))
                .orderBy(QInvoice.invoice.id.desc())
                .limit(1l)
                .fetchOne();
    }

    private long getTotalCount(Predicate predicate) {
        QInvoice qInvoice = QInvoice.invoice;

        return this.query.selectFrom(qInvoice)
                .where(predicate)
                .fetchCount();
    }

    private BigDecimal getTotalPrice(Predicate predicate) {
        return this.query.select(QInvoice.invoice.priceAfterDiscount.sum())
                .from(QInvoice.invoice)
                .where(predicate)
                .fetchOne();
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

    private List<Long> getInvoiceIdsPaginate(Pageable pageable, String rqlFilter, List<Long> companyIds, Predicate predicate, OrderSpecifierList orderSpecifierList) {
        JPAQuery<Invoice> invoiceJPAQuery = this.query.selectFrom(QInvoice.invoice)
                .where(predicate)
                .where(QInvoice.invoice.company.id.in(companyIds))
                .orderBy(orderSpecifierList.getOrders().toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (!rqlFilter.contains("invoice.company.id")) { // todo make refakt
            invoiceJPAQuery.where(QInvoice.invoice.company.id.in(companyIds));
        }

        return invoiceJPAQuery.select(QInvoice.invoice.id).fetch();
    }


    private Predicate buildPredicate(String rqlFilter) {
        Predicate predicate = new BooleanBuilder();

        if (rqlFilter.equals("")) {
            return predicate;
        }

        if (rqlFilter.contains("invoice.documentType==INVOICE")) {
            rqlFilter = rqlFilter.replace("invoice.documentType==INVOICE", "(invoice.documentType==INVOICE,invoice.documentType==TAX_DOCUMENT,invoice.documentType==SUMMARY_INVOICE)");
        }

        Map<String, String> filterPathMapping = new HashMap<>();

        filterPathMapping.put("invoice.id", "id");
        filterPathMapping.put("invoice.company.id", "company.id");
        filterPathMapping.put("invoice.title", "title");
        filterPathMapping.put("invoice.state", "state");
        filterPathMapping.put("invoice.document_type", "documentType");
        filterPathMapping.put("invoice.contact.id", "contact.id");
        filterPathMapping.put("invoice.contact.name", "contact.name");
        filterPathMapping.put("invoice.project.id", "project.id");
        filterPathMapping.put("invoice.documentType", "documentType");
        filterPathMapping.put("invoice.start", "createdDate");
        filterPathMapping.put("invoice.end", "createdDate");

        return RSQLQueryDslSupport.toPredicate(rqlFilter, QInvoice.invoice, filterPathMapping);
    }

    private OrderSpecifierList buildSort(String sortExpression) {
        DefaultSortParser sortParser = new DefaultSortParser();

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

        return sortParser.parse(sortExpression, QuerydslSortContext.withMapping(pathMapping));
    }
}
