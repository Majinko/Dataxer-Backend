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
public class QProjectRepositoryImpl implements QProjectRepository {
    private final JPAQueryFactory query;

    public QProjectRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    private Long total() {
        QProject qProject = QProject.project;

        return query.selectFrom(qProject).fetchCount();
    }

    @Override
    public Page<Project> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long appProfileId) {
        DefaultSortParser sortParser = new DefaultSortParser();
        DefaultFilterParser filterParser = new DefaultFilterParser();
        Predicate predicate = new BooleanBuilder();

        QProject qProject = QProject.project;

        Map<String, Path> pathMapping = ImmutableMap.<String, Path>builder()
                .put("project.id", QProject.project.id)
                .put("project.title", QProject.project.title)
                .put("project.number", QProject.project.number)
                .put("project.address", QProject.project.address)
                .put("project.contact.id", QProject.project.contact.id)
                .put("project.contact.name", QProject.project.contact.name)
                .build();

        if (!rqlFilter.equals("")) {
            predicate = filterParser.parse(rqlFilter, withBuilderAndParam(new QuerydslFilterBuilder(), new QuerydslFilterParam().setMapping(pathMapping)));
        }

        OrderSpecifierList orderSpecifierList = sortParser.parse(sortExpression, QuerydslSortContext.withMapping(pathMapping));

        List<Project> projectList = this.query.selectFrom(qProject)
                .leftJoin(qProject.contact).fetchJoin()
                .where(predicate)
                .where(qProject.appProfile.id.eq(appProfileId))
                .orderBy(orderSpecifierList.getOrders().toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(projectList, pageable, getTotalCount(predicate));
    }

    @Override
    public Project getById(Long id, Long appProfileId) {
        return query
                .selectFrom(QProject.project)
                .where(QProject.project.appProfile.id.eq(appProfileId))
                .where(QProject.project.id.eq(id))
                .leftJoin(QProject.project.contact).fetchJoin()
                .leftJoin(QProject.project.categories, QCategory.category).fetchJoin()
                .fetchOne();

    }

    @Override
    public List<Project> search(Long appProfileId, String queryString) {
        return query.selectFrom(QProject.project)
                .where(QProject.project.appProfile.id.eq(appProfileId))
                .where(QProject.project.title.containsIgnoreCase(queryString))
                .fetch();
    }

    @Override
    public List<Project> allHasCost(Long appProfileId) {
        return query.selectFrom(QProject.project)
                .where(QProject.project.appProfile.id.eq(appProfileId))
                .where(QProject.project.id.in(JPAExpressions.select(QCost.cost.project.id).from(QCost.cost).fetchAll()))
                .fetch();
    }

    @Override
    public List<Project> allHasInvoice(Long appProfileId) {
        return query.selectFrom(QProject.project)
                .where(QProject.project.appProfile.id.eq(appProfileId))
                .where(QProject.project.id.in(JPAExpressions.select(QInvoice.invoice.project.id).from(QInvoice.invoice).fetchAll()))
                .fetch();
    }

    @Override
    public List<Project> allHasPriceOffer(Long appProfileId) {
        return query.selectFrom(QProject.project)
                .where(QProject.project.appProfile.id.eq(appProfileId))
                .where(QProject.project.id.in(JPAExpressions.select(QPriceOffer.priceOffer.project.id).from(QPriceOffer.priceOffer).fetchAll()))
                .fetch();
    }

    @Override
    public List<Project> allHasUserTime(String uid, Long appProfileId) {
        return query.selectFrom(QProject.project)
                .where(QProject.project.appProfile.id.eq(appProfileId))
                .where(QProject.project.id.in(
                        JPAExpressions
                                .select(QTime.time1.project.id)
                                .from(QTime.time1)
                                .where(QTime.time1.user.uid.eq(uid))
                                .fetchAll())
                )
                .fetch();
    }

    @Override
    public List<Project> getAllByIds(List<Long> ids, Long appProfileId) {
        return this.query.selectFrom(QProject.project)
                .where(QProject.project.id.in(ids))
                .where(QProject.project.appProfile.id.eq(appProfileId))
                .fetch();
    }

    @Override
    public List<Project> allHasPriceOfferCostInvoice(Long appProfileId) {
        return query.selectFrom(QProject.project)
                .where(QProject.project.appProfile.id.eq(appProfileId))
                .where(QProject.project.id.in(this.projectIdsInDocument(appProfileId)))
                .fetch();
    }

    private List<Long> projectIdsInDocument(Long appProfileId) {
        Set<Long> projectIds = new HashSet<Long>();

        // add all price offer contact
        projectIds.addAll(
                this.query.select(QPriceOffer.priceOffer.project.id)
                        .from(QPriceOffer.priceOffer)
                        .where(QPriceOffer.priceOffer.project.id.isNotNull())
                        .where(QPriceOffer.priceOffer.appProfile.id.eq(appProfileId)).fetch()
        );

        // add all invoice contact
        projectIds.addAll(
                this.query.select(QInvoice.invoice.project.id)
                        .from(QInvoice.invoice)
                        .where(QInvoice.invoice.project.id.isNotNull())
                        .where(QInvoice.invoice.appProfile.id.eq(appProfileId)).fetch()
        );

        // cost all invoice contact
        projectIds.addAll(
                this.query.select(QCost.cost.project.id)
                        .from(QCost.cost)
                        .where(QCost.cost.project.id.isNotNull())
                        .where(QCost.cost.appProfile.id.eq(appProfileId)).fetch()
        );

        return new ArrayList<>(projectIds);
    }

    private long getTotalCount(Predicate predicate) {
        QProject qProject = QProject.project;

        return this.query.selectFrom(qProject)
                .where(predicate)
                .fetchCount();
    }
}
