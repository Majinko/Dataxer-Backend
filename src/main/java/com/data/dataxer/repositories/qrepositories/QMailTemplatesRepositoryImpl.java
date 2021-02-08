package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.MailTemplates;
import com.data.dataxer.models.domain.QMailTemplates;
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
public class QMailTemplatesRepositoryImpl implements QMailTemplatesRepository {

    private final JPAQueryFactory query;

    public QMailTemplatesRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<MailTemplates> getById(Long id, List<Long> companyIds) {
        QMailTemplates qMailTemplates = QMailTemplates.mailTemplates;

        return Optional.ofNullable(
            this.query.selectFrom(qMailTemplates)
                .where(qMailTemplates.id.eq(id))
                .where(qMailTemplates.company.id.in(companyIds))
                .fetchOne()
        );
    }

    @Override
    public Page<MailTemplates> paginate(Pageable pageable, String rqlFilter, String sortExpression, List<Long> companyIds) {
        DefaultSortParser sortParser = new DefaultSortParser();
        DefaultFilterParser filterParser = new DefaultFilterParser();
        Predicate predicate = new BooleanBuilder();

        QMailTemplates qMailTemplates = QMailTemplates.mailTemplates;

        Map<String, Path> pathMapping = ImmutableMap.<String, Path>builder()
                .put("mailTemplates.id", QMailTemplates.mailTemplates.id)
                .put("mailTemplates.emailSubject", QMailTemplates.mailTemplates.emailSubject)
                .put("mailTemplates.emailContent", QMailTemplates.mailTemplates.emailContent)
                .put("mailTemplates.company.id", QMailTemplates.mailTemplates.company.id)
                .build();

        if (!rqlFilter.equals("")) {
            predicate = filterParser.parse(rqlFilter, withBuilderAndParam(new QuerydslFilterBuilder(), new QuerydslFilterParam()
                    .setMapping(pathMapping)));
        }
        OrderSpecifierList orderSpecifierList = sortParser.parse(sortExpression, QuerydslSortContext.withMapping(pathMapping));

        List<MailTemplates> mailTemplatesList = this.query.selectFrom(qMailTemplates)
                .where(predicate)
                .where(qMailTemplates.company.id.in(companyIds))
                .orderBy(orderSpecifierList.getOrders().toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(mailTemplatesList, pageable, getTotalCount(predicate));
    }

    @Override
    public long updateByMailTemplates(MailTemplates mailTemplates, List<Long> companyIds) {
        QMailTemplates qMailTemplates = QMailTemplates.mailTemplates;

        return this.query.update(qMailTemplates)
                .set(qMailTemplates.emailSubject, mailTemplates.getEmailSubject())
                .set(qMailTemplates.emailContent, mailTemplates.getEmailContent())
                .where(qMailTemplates.id.eq(mailTemplates.getId()))
                .where(qMailTemplates.company.id.in(companyIds))
                .execute();
    }

    private long getTotalCount(Predicate predicate) {
        QMailTemplates qMailTemplates = QMailTemplates.mailTemplates;

        return this.query.selectFrom(qMailTemplates)
                .where(predicate)
                .fetchCount();
    }
}
