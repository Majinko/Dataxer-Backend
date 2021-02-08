package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.QTask;
import com.data.dataxer.models.domain.Task;
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
public class QTaskRepositoryImpl implements QTaskRepository {
    private final JPAQueryFactory query;

    public QTaskRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Task getById(Long id, List<Long> companyIds) {
        QTask qTask = QTask.task;

        return query
                .selectFrom(qTask)
                .where(qTask.company.id.in(companyIds))
                .where(qTask.id.eq(id))
                .leftJoin(qTask.files).fetchJoin()
                .leftJoin(qTask.user).fetchJoin()
                .leftJoin(qTask.userFrom).fetchJoin()
                .leftJoin(qTask.project).fetchJoin()
                .leftJoin(qTask.category).fetchJoin()
                .fetchOne();
    }


    @Override
    public Page<Task> paginate(Pageable pageable, String rqlFilter, String sortExpression, List<Long> companyIds) {
        DefaultSortParser sortParser = new DefaultSortParser();
        DefaultFilterParser filterParser = new DefaultFilterParser();
        Predicate predicate = new BooleanBuilder();

        QTask qTask = QTask.task;

        Map<String, Path> pathMapping = ImmutableMap.<String, Path>builder()
                .put("task.id", QTask.task.id)
                .build();

        if (!rqlFilter.equals("")) {
            predicate = filterParser.parse(rqlFilter, withBuilderAndParam(new QuerydslFilterBuilder(), new QuerydslFilterParam()
                    .setMapping(pathMapping)));
        }
        OrderSpecifierList orderSpecifierList = sortParser.parse(sortExpression, QuerydslSortContext.withMapping(pathMapping));

        List<Task> taskList = this.query.selectFrom(qTask)
                .leftJoin(qTask.user).fetchJoin()
                .leftJoin(qTask.userFrom).fetchJoin()
                .leftJoin(qTask.project).fetchJoin()
                .leftJoin(qTask.category).fetchJoin()
                .where(predicate)
                .where(qTask.company.id.in(companyIds))
                .orderBy(orderSpecifierList.getOrders().toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(taskList, pageable, getTotalCount(predicate));
    }

    private long getTotalCount(Predicate predicate) {
        QTask qTask = QTask.task;

        return this.query.selectFrom(qTask)
                .where(predicate)
                .fetchCount();
    }
}
