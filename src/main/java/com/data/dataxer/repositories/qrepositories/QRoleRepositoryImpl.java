package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.security.model.QPrivilege;
import com.data.dataxer.security.model.QRole;
import com.data.dataxer.security.model.Role;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.vineey.rql.filter.FilterContext.withBuilderAndParam;

@Repository
public class QRoleRepositoryImpl implements QRoleRepository {

    private final JPAQueryFactory query;

    public QRoleRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Role> paginate(Pageable pageable, String rqlFilter, String sortExpression, List<Long> companyIds) {
        DefaultSortParser sortParser = new DefaultSortParser();
        DefaultFilterParser filterParser = new DefaultFilterParser();

        Predicate predicate = new BooleanBuilder();

        Map<String, Path> pathMapping = ImmutableMap.<String, Path>builder()
                .put("role.id", QRole.role.id)
                .build();

        if (!rqlFilter.equals("")) {
            predicate = filterParser.parse(rqlFilter, withBuilderAndParam(new QuerydslFilterBuilder(), new QuerydslFilterParam().setMapping(pathMapping)));
        }

        OrderSpecifierList orderSpecifierList = sortParser.parse(sortExpression, QuerydslSortContext.withMapping(pathMapping));

        List<Long> roleIds = this.query.selectFrom(QRole.role)
                .where(predicate)
                .where(QRole.role.company.id.in(companyIds))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .select(QRole.role.id)
                .fetch();


        List<Role> roleList = this.query.selectFrom(QRole.role)
                .join(QRole.role.privileges, QPrivilege.privilege).fetchJoin()
                .where(QRole.role.id.in(roleIds))
                .orderBy(orderSpecifierList.getOrders().toArray(new OrderSpecifier[0]))
                .fetch().stream().distinct().collect(Collectors.toList());

        return new PageImpl<>(roleList, pageable, getTotalCount(predicate));
    }

    @Override
    public Optional<Role> getById(Long id, List<Long> companyIds) {
        return Optional.ofNullable(
                this.query.selectFrom(QRole.role)
                        .join(QRole.role.privileges, QPrivilege.privilege).fetchJoin()
                        .where(QRole.role.id.eq(id))
                        .where(QRole.role.company.id.in(companyIds))
                        .fetchOne()
        );
    }


    private long getTotalCount(Predicate predicate) {
        QRole qRole = QRole.role;

        return this.query.selectFrom(qRole)
                .where(predicate)
                .fetchCount();
    }
}
