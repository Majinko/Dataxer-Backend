package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.QDocumentNumberGenerator;
import com.data.dataxer.models.domain.DocumentNumberGenerator;
import com.data.dataxer.models.enums.DocumentType;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.vineey.rql.filter.FilterContext.withBuilderAndParam;

@Repository
public class QDocumentNumberGeneratorRepositoryImpl implements QDocumentNumberGeneratorRepository {
    private final JPAQueryFactory query;

    public QDocumentNumberGeneratorRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<DocumentNumberGenerator> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long appProfileId) {
        DefaultSortParser sortParser = new DefaultSortParser();
        DefaultFilterParser filterParser = new DefaultFilterParser();
        Predicate predicate = new BooleanBuilder();

        QDocumentNumberGenerator qDocumentNumberGenerator = QDocumentNumberGenerator.documentNumberGenerator;

        Map<String, Path> pathMapping = ImmutableMap.<String, Path>builder()
                .put("documentNumberGenerator.id", QDocumentNumberGenerator.documentNumberGenerator.id)
                .build();

        if (!rqlFilter.equals("")) {
            predicate = filterParser.parse(rqlFilter, withBuilderAndParam(new QuerydslFilterBuilder(), new QuerydslFilterParam()
                    .setMapping(pathMapping)));
        }
        OrderSpecifierList orderSpecifierList = sortParser.parse(sortExpression, QuerydslSortContext.withMapping(pathMapping));

        List<DocumentNumberGenerator> documentNumberGeneratorList = this.query.selectFrom(qDocumentNumberGenerator)
                .where(predicate)
                .where(qDocumentNumberGenerator.appProfile.id.eq(appProfileId))
                .orderBy(orderSpecifierList.getOrders().toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(documentNumberGeneratorList, pageable, getTotalCount(predicate));
    }

    @Override
    public Optional<DocumentNumberGenerator> getById(Long id, Long appProfileId) {
        QDocumentNumberGenerator qDocumentNumberGenerator = QDocumentNumberGenerator.documentNumberGenerator;

        DocumentNumberGenerator documentNumberGenerator = this.query
                .selectFrom(qDocumentNumberGenerator)
                .where(qDocumentNumberGenerator.id.eq(id))
                .orderBy(qDocumentNumberGenerator.id.desc())
                .fetchOne();

        return Optional.ofNullable(documentNumberGenerator);
    }

    @Override
    public Optional<DocumentNumberGenerator> getByIdSimple(Long id, Long appProfileId) {
        QDocumentNumberGenerator qDocumentNumberGenerator = QDocumentNumberGenerator.documentNumberGenerator;

        return Optional.ofNullable(this.query
                .selectFrom(qDocumentNumberGenerator)
                .where(qDocumentNumberGenerator.id.eq(id))
                .where(qDocumentNumberGenerator.appProfile.id.eq(appProfileId))
                .fetchOne());
    }

    @Override
    public DocumentNumberGenerator getDefaultByDocumentType(DocumentType documentType, Long companyId, Long appProfileId) {
        QDocumentNumberGenerator qDocumentNumberGenerator = QDocumentNumberGenerator.documentNumberGenerator;

        JPAQuery<DocumentNumberGenerator> query = this.query
                .selectFrom(qDocumentNumberGenerator)
                .leftJoin(QDocumentNumberGenerator.documentNumberGenerator.company).fetchJoin()
                .where(qDocumentNumberGenerator.isDefault.eq(true))
                .where(qDocumentNumberGenerator.company.id.eq(companyId))
                .where(qDocumentNumberGenerator.appProfile.id.eq(appProfileId));

        if (documentType.equals(DocumentType.PROFORMA)) {
            query.where(qDocumentNumberGenerator.type.eq(documentType));
        } else {
            // ak sa jedna o fakturu, danovy doklad, alebo vyuctovaciu fakturu pouzivam ciselnik na generovanie faktur
            query.where(qDocumentNumberGenerator.type.eq(DocumentType.INVOICE));
        }

        return query.fetchOne();
    }

    private long getTotalCount(Predicate predicate) {
        QDocumentNumberGenerator qDocumentNumberGenerator = QDocumentNumberGenerator.documentNumberGenerator;

        return this.query.selectFrom(qDocumentNumberGenerator)
                .where(predicate)
                .fetchCount();
    }
}
