package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.*;
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
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.*;

import static com.github.vineey.rql.filter.FilterContext.withBuilderAndParam;

@Repository
public class QContactRepositoryImpl implements QContactRepository {
    private final JPAQueryFactory query;

    private final QContact CONTACT = QContact.contact;

    public QContactRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Contact> allWithProjects(Long appProfileId) {
        QProject PROJECT = QProject.project;

        return query
                .selectFrom(CONTACT)
                .where(CONTACT.appProfile.id.eq(appProfileId))
                .join(CONTACT.projects, PROJECT)
                .fetch();
    }

    @Override
    public Page<Contact> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long appProfileId) {
        DefaultSortParser sortParser = new DefaultSortParser();
        DefaultFilterParser filterParser = new DefaultFilterParser();
        Predicate predicate = new BooleanBuilder();

        QContact qContact = QContact.contact;

        Map<String, Path> pathMapping = ImmutableMap.<String, Path>builder()
                .put("contact.id", QContact.contact.id)
                .put("contact.name", QContact.contact.name)
                .put("contact.email", QContact.contact.email)
                .build();

        if (!rqlFilter.equals("")) {
            predicate = filterParser.parse(rqlFilter, withBuilderAndParam(new QuerydslFilterBuilder(), new QuerydslFilterParam()
                    .setMapping(pathMapping)));
        }
        OrderSpecifierList orderSpecifierList = sortParser.parse(sortExpression, QuerydslSortContext.withMapping(pathMapping));

        List<Contact> contactList = this.query.selectFrom(qContact)
                // .leftJoin(qContact.projects).fetchJoin()
                .where(predicate)
                .where(qContact.appProfile.id.eq(appProfileId))
                .orderBy(orderSpecifierList.getOrders().toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(contactList, pageable, getTotalCount(predicate, appProfileId));
    }

    @Override
    public Optional<Contact> getById(Long id, Long appProfileId) {
        return Optional.ofNullable(this.query.selectFrom(CONTACT)
                .where(CONTACT.id.eq(id))
                .where(CONTACT.appProfile.id.eq(appProfileId))
                .fetchOne());
    }

    @Override
    public List<Contact> getAllByIds(List<Long> contactIds, Long appProfileId) {
        return this.query.selectFrom(CONTACT)
                .where(CONTACT.id.in(contactIds))
                .where(CONTACT.appProfile.id.eq(appProfileId))
                .fetch();
    }

    @Override
    public List<Contact> allHasCost(Long appProfileId) {
        return query.selectFrom(QContact.contact)
                .where(QContact.contact.appProfile.id.eq(appProfileId))
                .where(QContact.contact.id.in(JPAExpressions.select(QCost.cost.contact.id).from(QCost.cost).fetchAll()))
                .fetch();
    }

    @Override
    public List<Contact> allHasInvoice(Long appProfileId) {
        return query.selectFrom(QContact.contact)
                .where(QContact.contact.appProfile.id.eq(appProfileId))
                .where(QContact.contact.id.in(JPAExpressions.select(QInvoice.invoice.contact.id).from(QInvoice.invoice).fetchAll()))
                .fetch();
    }

    @Override
    public List<Contact> allHasPriceOffer(Long appProfileId) {
        return query.selectFrom(QContact.contact)
                .where(QContact.contact.appProfile.id.eq(appProfileId))
                .where(
                        QContact.contact.id.in(JPAExpressions.select(QPriceOffer.priceOffer.contact.id)
                                .from(QPriceOffer.priceOffer)
                                .fetchAll())
                )
                .fetch();
    }

    @Override
    public List<Contact> allHasProject(Long appProfileId) {
        return query.selectFrom(QContact.contact)
                .where(QContact.contact.appProfile.id.eq(appProfileId))
                .where(QContact.contact.id.in(JPAExpressions.select(QProject.project.contact.id).from(QProject.project).fetchAll()))
                .fetch();
    }

    @Override
    public List<Contact> allHasPriceOfferCostInvoice(Long appProfileId) {
        return query.selectFrom(QContact.contact)
                .where(QContact.contact.appProfile.id.eq(appProfileId))
                .where(
                        QContact.contact.id.in(this.contactIdsInDocuments(appProfileId))
                )
                .fetch();
    }

    private List<Long> contactIdsInDocuments(Long appProfileId) {
        Set<Long> contactIds = new HashSet<Long>();

        // add all price offer contact
        contactIds.addAll(
                this.query.select(QPriceOffer.priceOffer.contact.id).from(QPriceOffer.priceOffer).where(QPriceOffer.priceOffer.appProfile.id.eq(appProfileId)).fetch()
        );

        // add all invoice contact
        contactIds.addAll(
                this.query.select(QInvoice.invoice.contact.id).from(QInvoice.invoice).where(QInvoice.invoice.appProfile.id.eq(appProfileId)).fetch()
        );

        // cost all invoice contact
        contactIds.addAll(
                this.query.select(QCost.cost.contact.id).from(QCost.cost).where(QCost.cost.appProfile.id.eq(appProfileId)).fetch()
        );

        return new ArrayList<>(contactIds);
    }

    private long getTotalCount(Predicate predicate, Long appProfileId) {
        QContact qContact = QContact.contact;

        return this.query.selectFrom(qContact)
                .where(predicate)
                .where(QContact.contact.appProfile.id.eq(appProfileId))
                .fetchCount();
    }
}