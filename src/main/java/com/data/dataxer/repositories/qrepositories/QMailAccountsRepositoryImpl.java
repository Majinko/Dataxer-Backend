package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.MailAccounts;
import com.data.dataxer.models.domain.QMailAccounts;
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

import static com.github.vineey.rql.filter.FilterContext.withBuilderAndParam;

@Repository
public class QMailAccountsRepositoryImpl implements QMailAccountsRepository {

    private final JPAQueryFactory query;

    public QMailAccountsRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<MailAccounts> getById(Long id, Long appProfileId) {
        QMailAccounts qMailAccounts = QMailAccounts.mailAccounts;

        return Optional.ofNullable(
                this.query.selectFrom(qMailAccounts)
                        .where(qMailAccounts.id.eq(id))
                        .where(qMailAccounts.appProfile.id.eq(appProfileId))
                        .fetchOne()
        );
    }

    @Override
    public long updateByMailAccounts(MailAccounts mailAccounts, Long appProfileId) {
        QMailAccounts qMailAccounts = QMailAccounts.mailAccounts;

        return this.query.update(qMailAccounts)
                .set(qMailAccounts.hostName, mailAccounts.getHostName())
                .set(qMailAccounts.userName, mailAccounts.getUserName())
                .set(qMailAccounts.port, mailAccounts.getPort())
                .set(qMailAccounts.password, mailAccounts.getPassword())
                .set(qMailAccounts.state, mailAccounts.getState())
                .where(qMailAccounts.id.eq(mailAccounts.getId()))
                .where(qMailAccounts.appProfile.id.eq(appProfileId))
                .execute();
    }

    @Override
    public Page<MailAccounts> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long appProfileId) {
        DefaultSortParser sortParser = new DefaultSortParser();
        DefaultFilterParser filterParser = new DefaultFilterParser();
        Predicate predicate = new BooleanBuilder();

        QMailAccounts qMailAccounts = QMailAccounts.mailAccounts;

        Map<String, Path> pathMapping = ImmutableMap.<String, Path>builder()
                .put("mailAccounts.id", QMailAccounts.mailAccounts.id)
                .put("mailAccounts.userName", QMailAccounts.mailAccounts.userName)
                .put("mailAccounts.hostName", QMailAccounts.mailAccounts.hostName)
                .put("mailAccounts.company.id", QMailAccounts.mailAccounts.company.id)
                .build();

        if (!rqlFilter.equals("")) {
            predicate = filterParser.parse(rqlFilter, withBuilderAndParam(new QuerydslFilterBuilder(), new QuerydslFilterParam()
                    .setMapping(pathMapping)));
        }
        OrderSpecifierList orderSpecifierList = sortParser.parse(sortExpression, QuerydslSortContext.withMapping(pathMapping));

        List<MailAccounts> mailAccountsList = this.query.selectFrom(qMailAccounts)
                .where(predicate)
                .where(qMailAccounts.appProfile.id.eq(appProfileId))
                .orderBy(orderSpecifierList.getOrders().toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(mailAccountsList, pageable, getTotalCount(predicate));
    }

    @Override
    public Optional<MailAccounts> getByCompaniesId(Long appProfileId) {
        QMailAccounts qMailAccounts = QMailAccounts.mailAccounts;

        return Optional.ofNullable(
                this.query.selectFrom(qMailAccounts)
                    .where(qMailAccounts.appProfile.id.eq(appProfileId))
                    .fetchOne()
        );
    }

    private long getTotalCount(Predicate predicate) {
        QMailAccounts qMailAccounts = QMailAccounts.mailAccounts;

        return this.query.selectFrom(qMailAccounts)
                .where(predicate)
                .fetchCount();
    }
}
