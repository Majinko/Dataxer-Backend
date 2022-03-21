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
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.vineey.rql.filter.FilterContext.withBuilderAndParam;

@Repository
public class QDemandRepositoryImpl implements QDemandRepository {
    private final JPAQueryFactory query;

    public QDemandRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Demand> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long appProfileId) {
        DefaultSortParser sortParser = new DefaultSortParser();
        DefaultFilterParser filterParser = new DefaultFilterParser();
        Predicate predicate = new BooleanBuilder();

        QDemand qDemand = QDemand.demand;

        Map<String, Path> pathMapping = ImmutableMap.<String, Path>builder()
                .put("demand.id", QDemand.demand.id)
                .put("demand.start", QDemand.demand.createdDate)
                .put("demand.end", QDemand.demand.createdDate)
                .build();

        if (!rqlFilter.equals("")) {
            predicate = filterParser.parse(rqlFilter, withBuilderAndParam(new QuerydslFilterBuilder(), new QuerydslFilterParam()
                    .setMapping(pathMapping)));
        }

        // todo spravit refaktoring a porozmyslat ako parovat lepsie dopyty ktore mi prichadzaju a nevytvaral som ich ja
        List<Company> myCompanies = this.query
                .selectFrom(QCompany.company)
                .where(QCompany.company.appProfile.id.eq(appProfileId))
                .fetch();

        OrderSpecifierList orderSpecifierList = sortParser.parse(sortExpression, QuerydslSortContext.withMapping(pathMapping));

        List<Demand> demandList = this.query.selectFrom(qDemand)
                .where(predicate)
                .where(qDemand.appProfile.id.eq(appProfileId).or(
                        QContact.contact.in(this.supplierForDemand(myCompanies)) // najde dopyty pre dodavatela
                ))
                .leftJoin(QDemand.demand.contacts, QContact.contact).fetchJoin()
                .leftJoin(qDemand.project, QProject.project).fetchJoin()
                .leftJoin(qDemand.company, QCompany.company).fetchJoin()
                .orderBy(orderSpecifierList.getOrders().toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .peek(demand -> demand.setInternal(myCompanies.contains(demand.getCompany())))
                .collect(Collectors.toList());

        return new PageImpl<>(demandList, pageable, getTotalCount(predicate, appProfileId));
    }

    private List<Company> myCompanies(Long appProfileId) {
        // todo spravit refaktoring a porozmyslat ako parovat lepsie dopyty ktore mi prichadzaju a nevytvaral som ich ja
        return this.query
                .selectFrom(QCompany.company)
                .where(QCompany.company.appProfile.id.eq(appProfileId))
                .fetch();
    }

    private List<Contact> supplierForDemand(List<Company> companies) {
        return this.query.selectFrom(QContact.contact).where(QContact.contact.cin.in(
                companies.stream().map(Company::getCin).collect(Collectors.toList())
        )).fetch();
    }

    @Override
    public Demand getById(Long id, Long appProfileId) {
        // todo spravit refaktoring a porozmyslat ako parovat lepsie dopyty ktore mi prichadzaju a nevytvaral som ich ja
        List<Company> myCompanies = this.query
                .selectFrom(QCompany.company)
                .where(QCompany.company.appProfile.id.eq(appProfileId))
                .fetch();

        QDemand qDemand = QDemand.demand;

        Demand demand = query.selectFrom(qDemand)
                .where(qDemand.appProfile.id.eq(appProfileId).or(
                        QContact.contact.in(this.supplierForDemand(myCompanies)) // najde dopyty pre dodavatela
                ))
                .where(qDemand.id.eq(id))
                .leftJoin(qDemand.project, QProject.project).fetchJoin()
                .leftJoin(qDemand.company, QCompany.company).fetchJoin()
                .leftJoin(qDemand.contacts, QContact.contact).fetchJoin()
                .fetchOne();

        if (demand != null) {
            demand.setInternal(myCompanies.contains(demand.getCompany()));

            demand.setPacks(
                    this.query.selectFrom(QDemandPack.demandPack)
                            .where(QDemandPack.demandPack.demand.id.eq(demand.getId()))
                            //.where(QDemandPack.demandPack.appProfile.id.eq(appProfileId))
                            .leftJoin(QDemandPack.demandPack.packItems, QDemandPackItem.demandPackItem).fetchJoin()
                            .leftJoin(QDemandPackItem.demandPackItem.item, QItem.item).fetchJoin()
                            .leftJoin(QDemandPackItem.demandPackItem.category, QCategory.category).fetchJoin()
                            .distinct()
                            .fetch()
            );
        }

        return demand;
    }

    @Override
    public Demand getLastDemandByMonthAndYear(LocalDate date, Long companyId, Long appProfileId) {
        return this.query.selectFrom(QDemand.demand)
                .where(QDemand.demand.createdDate.month().eq(date.getMonthValue()))
                .where(QDemand.demand.createdDate.year().eq(date.getYear()))
                .where(QDemand.demand.appProfile.id.eq(appProfileId))
                .where(QDemand.demand.company.id.eq(companyId))
                .orderBy(QDemand.demand.id.desc())
                .limit(1L)
                .fetchOne();
    }

    @Override
    public Demand getLastDemandByQuarterAndYear(LocalDate date, Long companyId, Long appProfileId) {
        return this.query.selectFrom(QDemand.demand)
                .where(QDemand.demand.createdDate.month().divide(3.0).ceil().eq(date.get(IsoFields.QUARTER_OF_YEAR)))
                .where(QDemand.demand.createdDate.year().eq(date.getYear()))
                .where(QDemand.demand.appProfile.id.eq(appProfileId))
                .where(QDemand.demand.company.id.eq(companyId))
                .orderBy(QDemand.demand.id.desc())
                .limit(1L)
                .fetchOne();
    }

    @Override
    public Demand getLastDemandByDayAndMonthAndYear(LocalDate localDate, Long companyId, Long appProfileId) {
        return this.query.selectFrom(QDemand.demand)
                .where(QDemand.demand.company.id.eq(companyId))
                .where(QDemand.demand.appProfile.id.eq(appProfileId))
                .where(QDemand.demand.createdDate.dayOfMonth().eq(localDate.getDayOfMonth()))
                .where(QDemand.demand.createdDate.month().eq(localDate.getMonthValue()))
                .where(QDemand.demand.createdDate.year().eq(localDate.getYear()))
                .orderBy(QDemand.demand.id.desc())
                .limit(1l)
                .fetchOne();
    }

    @Override
    public Demand getLastDemandByYear(LocalDate date, Long companyId, Long defaultProfileId) {
        return this.query.selectFrom(QDemand.demand)
                .where(QDemand.demand.createdDate.year().eq(date.getYear()))
                .where(QDemand.demand.appProfile.id.eq(defaultProfileId))
                .where(QDemand.demand.company.id.eq(companyId))
                .orderBy(QDemand.demand.id.desc())
                .limit(1L)
                .fetchOne();
    }


    private long getTotalCount(Predicate predicate, Long appProfileId) {
        QDemand qDemand = QDemand.demand;

        return this.query.selectFrom(qDemand)
                .where(qDemand.appProfile.id.eq(appProfileId))
                .where(predicate)
                .fetchCount();
    }
}
