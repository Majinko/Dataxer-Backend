package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Cost;
import com.data.dataxer.models.domain.QCategory;
import com.data.dataxer.models.domain.QCost;
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
import java.math.BigDecimal;
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
    public Page<Cost> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long companyId) {
        DefaultSortParser sortParser = new DefaultSortParser();
        DefaultFilterParser filterParser = new DefaultFilterParser();
        Predicate predicate = new BooleanBuilder();

        QCost qCost = QCost.cost;

        Map<String, Path> pathMapping = ImmutableMap.<String, Path>builder()
                .put("cost.id", QCost.cost.id)
                .build();

        if (!rqlFilter.equals("")) {
            predicate = filterParser.parse(rqlFilter, withBuilderAndParam(new QuerydslFilterBuilder(), new QuerydslFilterParam()
                    .setMapping(pathMapping)));
        }
        OrderSpecifierList orderSpecifierList = sortParser.parse(sortExpression, QuerydslSortContext.withMapping(pathMapping));

        List<Long> costIds = this.query.selectFrom(qCost)
                .where(predicate)
                .where(qCost.company.id.eq(companyId))
                .orderBy(orderSpecifierList.getOrders().toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .select(qCost.id).fetch();

        List<Cost> costList = this.query.selectFrom(qCost)
                .leftJoin(qCost.contact).fetchJoin()
                .leftJoin(qCost.project).fetchJoin()
                .join(QCost.cost.categories, QCategory.category).fetchJoin()
                .where(qCost.id.in(costIds))
                .fetch();


        return new PageImpl<>(costList, pageable, getTotalCount(predicate));
    }

    @Override
    public Optional<Cost> getById(Long id, Long companyId) {
        return Optional.ofNullable(
                this.constructGetAllByIdAndCompanyId(id, companyId).fetchOne()
        );
    }

    @Override
    public Optional<Cost> getByIdWithRelation(Long id, Long companyId) {
        Cost cost = this.constructGetAllByIdAndCompanyId(id, companyId)
                .leftJoin(QCost.cost.contact).fetchJoin()
                .leftJoin(QCost.cost.project).fetchJoin()
                .leftJoin(QCost.cost.files).fetchJoin()
                .fetchOne();

        if (cost != null) {
            cost.setCategories(
                    Objects.requireNonNull(this.constructGetAllByIdAndCompanyId(id, companyId)
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

    private JPAQuery<Cost> constructGetAllByIdAndCompanyId(Long id, Long companyId) {
        return query.selectFrom(QCost.cost)
                .where(QCost.cost.company.id.eq(companyId))
                .where(QCost.cost.id.eq(id));
    }

    private long getTotalCount(Predicate predicate) {
        QCost qCost = QCost.cost;

        return this.query.selectFrom(qCost)
                .where(predicate)
                .fetchCount();
    }
}
