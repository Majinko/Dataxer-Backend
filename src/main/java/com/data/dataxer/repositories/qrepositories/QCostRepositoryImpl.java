package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Cost;
import com.data.dataxer.models.domain.QCategory;
import com.data.dataxer.models.domain.QCost;
import org.springframework.data.domain.Page;
import com.data.dataxer.models.page.CustomPageImpl;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.github.vineey.rql.filter.FilterContext.withBuilderAndParam;

@Repository
public class QCostRepositoryImpl implements QCostRepository {

    private final JPAQueryFactory query;

    public QCostRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Cost> paginate(Pageable pageable, String rqlFilter, String sortExpression, List<Long> companyIds) {
        DefaultSortParser sortParser = new DefaultSortParser();
        DefaultFilterParser filterParser = new DefaultFilterParser();
        Predicate predicate = new BooleanBuilder();

        QCost qCost = QCost.cost;

        Map<String, Path> pathMapping = ImmutableMap.<String, Path>builder()
                .put("cost.id", QCost.cost.id)
                .put("cost.company.id", QCost.cost.company.id)
                .put("cost.title", QCost.cost.title)
                .put("cost.state", QCost.cost.state)
                .put("cost.contact.id", QCost.cost.contact.id)
                .put("cost.contact.name", QCost.cost.contact.name)
                .put("cost.project.id", QCost.cost.project.id)
                .put("cost.documentType", QCost.cost.type)
                .put("cost.start", QCost.cost.createdDate)
                .put("cost.end", QCost.cost.createdDate)
                .build();

        if (!rqlFilter.equals("")) {
            predicate = filterParser.parse(rqlFilter, withBuilderAndParam(new QuerydslFilterBuilder(), new QuerydslFilterParam()
                    .setMapping(pathMapping)));
        }

        OrderSpecifierList orderSpecifierList = sortParser.parse(sortExpression, QuerydslSortContext.withMapping(pathMapping));

        List<Long> costIds = this.returnCostsIdsForPaginate(pageable, rqlFilter, companyIds, predicate, orderSpecifierList);

        List<Cost> costList = this.query.selectFrom(qCost)
                .leftJoin(qCost.contact).fetchJoin()
                .leftJoin(qCost.project).fetchJoin()
                .join(QCost.cost.categories, QCategory.category).fetchJoin()
                .where(qCost.id.in(costIds))
                .orderBy(QCost.cost.id.desc())
                .distinct()
                .fetch();

        return new CustomPageImpl<Cost>(costList, pageable, getTotalCount(predicate), getTotalPrice(predicate));
    }

    @Override
    public Optional<Cost> getById(Long id, List<Long> companyIds) {
        return Optional.ofNullable(
                this.constructGetAllByIdAndCompanyId(id, companyIds).fetchOne()
        );
    }

    @Override
    public Optional<Cost> getByIdWithRelation(Long id, List<Long> companyIds) {
        Cost cost = this.constructGetAllByIdAndCompanyId(id, companyIds)
                .leftJoin(QCost.cost.contact).fetchJoin()
                .leftJoin(QCost.cost.project).fetchJoin()
                .leftJoin(QCost.cost.files).fetchJoin()
                .fetchOne();

        if (cost != null) {
            cost.setCategories(
                    Objects.requireNonNull(this.constructGetAllByIdAndCompanyId(id, companyIds)
                                    .leftJoin(QCost.cost.categories).fetchJoin()
                                    .fetchOne())
                            .getCategories()
            );
        }

        return Optional.ofNullable(cost);
    }

    @Override
    public List<Cost> getCostsWhereCategoryIdIn(List<Long> categoryIds, Integer year, Long companyId) {
        return this.query.selectFrom(QCost.cost)
                .where(QCost.cost.deliveredDate.year().eq(year))
                .join(QCost.cost.categories, QCategory.category)
                .where(QCategory.category.id.in(categoryIds))
                .where(QCost.cost.company.id.eq(companyId))
                .fetch();
    }

    @Override
    public BigDecimal getProjectCostsForYears(Integer firstYear, Integer lastYear, Long projectId, Long companyId) {
        return this.query.select(QCost.cost.totalPrice.sum())
                .from(QCost.cost)
                .where(QCost.cost.isRepeated.eq(Boolean.FALSE))
                .where(QCost.cost.isInternal.eq(Boolean.FALSE))
                .where(QCost.cost.deliveredDate.year().goe(firstYear))
                .where(QCost.cost.deliveredDate.year().loe(lastYear))
                .where(QCost.cost.company.id.eq(companyId))
                .where(QCost.cost.project.id.eq(projectId))
                .fetchOne();
    }

    @Override
    public List<Integer> getCostsYears(Long companyId) {
        return this.query.select(QCost.cost.deliveredDate.year())
                .from(QCost.cost)
                .where(QCost.cost.company.id.eq(companyId))
                .groupBy(QCost.cost.deliveredDate.year())
                .fetch();
    }

    @Override
    public BigDecimal getProjectTotalCostBetweenYears(LocalDate firstYearStart, LocalDate lastYearEnd, Boolean isInternal, Boolean isRepeated, List<Long> categoriesIdInProjectCost, List<Long> companyIds) {
        return this.query.from(QCost.cost)
                .select(QCost.cost.price.sum())
                .where(QCost.cost.deliveredDate.gt(firstYearStart))
                .where(QCost.cost.deliveredDate.lt(lastYearEnd))
                .where(QCost.cost.isInternal.eq(isInternal))
                .where(QCost.cost.isRepeated.eq(isRepeated))
                .where(QCost.cost.company.id.in(companyIds))
                .where(QCategory.category.id.in(categoriesIdInProjectCost))
                .join(QCost.cost.categories, QCategory.category)
                .fetchOne();
    }

    @Override
    public Cost getLastCost(Long companyId) {
        return this.query.selectFrom(QCost.cost)
                .where(QCost.cost.company.id.eq(companyId))
                .orderBy(QCost.cost.id.desc())
                .limit(1L)
                .fetchOne();
    }

    private JPAQuery<Cost> constructGetAllByIdAndCompanyId(Long id, List<Long> companyIds) {
        return query.selectFrom(QCost.cost)
                .where(QCost.cost.company.id.in(companyIds))
                .where(QCost.cost.id.eq(id));
    }

    private long getTotalCount(Predicate predicate) {
        QCost qCost = QCost.cost;

        return this.query.selectFrom(qCost)
                .where(predicate)
                .fetchCount();
    }

    private BigDecimal getTotalPrice(Predicate predicate) {
        return this.query
                .select(QCost.cost.price.sum())
                .from(QCost.cost)
                .where(predicate)
                .fetchOne();
    }

    private List<Long> returnCostsIdsForPaginate(Pageable pageable, String rqlFilter, List<Long> companyIds, Predicate predicate, OrderSpecifierList orderSpecifierList) {
        JPAQuery<Cost> costJPAQuery = this.query.selectFrom(QCost.cost)
                .where(predicate)
                .orderBy(orderSpecifierList.getOrders().toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (!rqlFilter.contains("cost.company.id")) {
            costJPAQuery.where(QCost.cost.company.id.in(companyIds));
        } // todo somethnig can hack, only change company id in header query params manual alebo nejaku grupu pre usera

        return costJPAQuery.select(QCost.cost.id).fetch();
    }
}
