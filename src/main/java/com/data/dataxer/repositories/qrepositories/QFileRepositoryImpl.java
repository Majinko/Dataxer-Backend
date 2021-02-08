package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.File;
import com.data.dataxer.models.domain.QFile;
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
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.vineey.rql.filter.FilterContext.withBuilderAndParam;

@Repository
public class QFileRepositoryImpl implements QFileRepository{

    private final JPAQueryFactory query;
    private final EntityManager entityManager;

    public QFileRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager.getEntityManagerFactory().createEntityManager();
        this.query = new JPAQueryFactory(this.entityManager);
    }

    @Override
    public Optional<File> getByNameAndCompanyIds(String fileName, Long companyId, Boolean disableFilter) {
        QFile qFile = QFile.file;

        if (!disableFilter) {
            this.entityManager.unwrap(Session.class).enableFilter("companyCondition").setParameter("companyId", companyId);
        }

        return Optional.ofNullable(this.query.selectFrom(qFile)
                .where(qFile.name.eq(fileName))
                .fetchOne());
    }

    @Override
    public Page<File> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long companyId, Boolean disableFilter) {
        DefaultSortParser sortParser = new DefaultSortParser();
        DefaultFilterParser filterParser = new DefaultFilterParser();
        Predicate predicate = new BooleanBuilder();

        QFile qFile = QFile.file;

        Map<String, Path> pathMapping = ImmutableMap.<String, Path>builder()
                .put("file.id", QFile.file.id)
                .build();

        if (!rqlFilter.equals("")) {
            predicate = filterParser.parse(rqlFilter, withBuilderAndParam(new QuerydslFilterBuilder(), new QuerydslFilterParam()
                    .setMapping(pathMapping)));
        }
        OrderSpecifierList orderSpecifierList = sortParser.parse(sortExpression, QuerydslSortContext.withMapping(pathMapping));

        if (!disableFilter) {
            this.entityManager.unwrap(Session.class).enableFilter("companyCondition").setParameter("companyId", companyId);
        }

        List<File> fileList = this.query.selectFrom(qFile)
                .where(predicate)
                .orderBy(orderSpecifierList.getOrders().toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(fileList, pageable, getTotalCount(predicate));
    }

    private long getTotalCount(Predicate predicate) {
        QFile qFile = QFile.file;

        return this.query.selectFrom(qFile)
                .where(predicate)
                .fetchCount();
    }

}
