package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Project;
import com.data.dataxer.models.domain.QProject;
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
import java.util.List;
import java.util.Map;

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
    public Page<Project> paginate(Pageable pageable, String rqlFilter, String sortExpression, List<Long> companyIds) {
        DefaultSortParser sortParser = new DefaultSortParser();
        DefaultFilterParser filterParser = new DefaultFilterParser();
        Predicate predicate = new BooleanBuilder();

        QProject qProject = QProject.project;

        Map<String, Path> pathMapping = ImmutableMap.<String, Path>builder()
                .put("project.id", QProject.project.id)
                .build();

        if (!rqlFilter.equals("")) {
            predicate = filterParser.parse(rqlFilter, withBuilderAndParam(new QuerydslFilterBuilder(), new QuerydslFilterParam()
                    .setMapping(pathMapping)));
        }
        OrderSpecifierList orderSpecifierList = sortParser.parse(sortExpression, QuerydslSortContext.withMapping(pathMapping));

        List<Project> projectList = this.query.selectFrom(qProject)
                .leftJoin(qProject.contact).fetchJoin()
                .where(predicate)
                .where(qProject.company.id.in(companyIds))
                .orderBy(orderSpecifierList.getOrders().toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(projectList, pageable, getTotalCount(predicate));
    }

    @Override
    public Project getById(Long id, List<Long> companyIds) {
        QProject qProject = QProject.project;

        return query
                .selectFrom(qProject)
                .where(qProject.company.id.in(companyIds))
                .where(qProject.id.eq(id))
                .leftJoin(qProject.contact)
                .fetchJoin()
                .fetchOne();

    }

    @Override
    public List<Project> search(List<Long> companyIds, String queryString) {
        return query.selectFrom(QProject.project)
                .where(QProject.project.company.id.in(companyIds))
                .where(QProject.project.title.containsIgnoreCase(queryString))
                .fetch();
    }

    private long getTotalCount(Predicate predicate) {
        QProject qProject = QProject.project;

        return this.query.selectFrom(qProject)
                .where(predicate)
                .fetchCount();
    }
}
