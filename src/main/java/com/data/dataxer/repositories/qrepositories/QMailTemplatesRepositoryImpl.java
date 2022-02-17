package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.MailTemplate;
import com.data.dataxer.models.domain.QMailTemplate;
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
    public Optional<MailTemplate> getById(Long id, Long appProfileId) {
        return Optional.ofNullable(
            this.query.selectFrom(QMailTemplate.mailTemplate)
                .where(QMailTemplate.mailTemplate.id.eq(id))
                .where(QMailTemplate.mailTemplate.appProfile.id.eq(appProfileId))
                .fetchOne()
        );
    }

    @Override
    public Page<MailTemplate> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long appProfileId) {
        DefaultSortParser sortParser = new DefaultSortParser();
        DefaultFilterParser filterParser = new DefaultFilterParser();
        Predicate predicate = new BooleanBuilder();

        Map<String, Path> pathMapping = ImmutableMap.<String, Path>builder()
                .put("mailTemplates.id", QMailTemplate.mailTemplate.id)
                .put("mailTemplates.emailSubject", QMailTemplate.mailTemplate.emailSubject)
                .put("mailTemplates.emailContent", QMailTemplate.mailTemplate.emailContent)
                .put("mailTemplates.company.id", QMailTemplate.mailTemplate.company.id)
                .build();

        if (!rqlFilter.equals("")) {
            predicate = filterParser.parse(rqlFilter, withBuilderAndParam(new QuerydslFilterBuilder(), new QuerydslFilterParam()
                    .setMapping(pathMapping)));
        }
        OrderSpecifierList orderSpecifierList = sortParser.parse(sortExpression, QuerydslSortContext.withMapping(pathMapping));

        List<MailTemplate> mailTemplatesList = this.query.selectFrom(QMailTemplate.mailTemplate)
                .where(predicate)
                .where(QMailTemplate.mailTemplate.appProfile.id.eq(appProfileId))
                .orderBy(orderSpecifierList.getOrders().toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(mailTemplatesList, pageable, getTotalCount(predicate));
    }

    @Override
    public long updateByMailTemplates(MailTemplate mailTemplates, Long companyId) {
        return this.query.update(QMailTemplate.mailTemplate)
                .set(QMailTemplate.mailTemplate.emailSubject, mailTemplates.getEmailSubject())
                .set(QMailTemplate.mailTemplate.emailContent, mailTemplates.getEmailContent())
                .where(QMailTemplate.mailTemplate.id.eq(mailTemplates.getId()))
                .where(QMailTemplate.mailTemplate.company.id.eq(companyId))
                .execute();
    }

    private long getTotalCount(Predicate predicate) {
        return this.query.selectFrom(QMailTemplate.mailTemplate)
                .where(predicate)
                .fetchCount();
    }
}
