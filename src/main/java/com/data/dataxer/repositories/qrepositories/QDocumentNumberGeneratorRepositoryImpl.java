package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.QDocumentNumberGenerator;
import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.DocumentNumberGenerator;
import com.data.dataxer.models.enums.DocumentType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class QDocumentNumberGeneratorRepositoryImpl implements QDocumentNumberGeneratorRepository{
    private final JPAQueryFactory query;

    public QDocumentNumberGeneratorRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<DocumentNumberGenerator> paginate(Pageable pageable, Filter filter, List<Long> companyIds) {
        QDocumentNumberGenerator qDocumentNumberGenerator = QDocumentNumberGenerator.documentNumberGenerator;

        BooleanBuilder filterCondition = new BooleanBuilder();

        if (filter != null && !filter.isEmpty()) {
            filterCondition = filter.buildNumberGeneratorFilterPredicate();
        }

        List<DocumentNumberGenerator> documentNumberGenerators = this.query
                .selectFrom(qDocumentNumberGenerator)
                .where(filterCondition)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(qDocumentNumberGenerator.id.desc())
                .fetch();

        return new PageImpl<>(documentNumberGenerators, pageable, total());
    }

    @Override
    public Optional<DocumentNumberGenerator> getById(Long id, List<Long> companyIds) {
        QDocumentNumberGenerator qDocumentNumberGenerator = QDocumentNumberGenerator.documentNumberGenerator;

        DocumentNumberGenerator documentNumberGenerator = this.query
                .selectFrom(qDocumentNumberGenerator)
                .where(qDocumentNumberGenerator.id.eq(id))
                .orderBy(qDocumentNumberGenerator.id.desc())
                .fetchOne();

        return Optional.ofNullable(documentNumberGenerator);
    }

    @Override
    public Optional<DocumentNumberGenerator> getByIdSimple(Long id, List<Long> companyIds) {
        QDocumentNumberGenerator qDocumentNumberGenerator = QDocumentNumberGenerator.documentNumberGenerator;

        return Optional.ofNullable(this.query
                .selectFrom(qDocumentNumberGenerator)
                .where(qDocumentNumberGenerator.id.eq(id))
                .where(qDocumentNumberGenerator.company.id.in(companyIds))
                .fetchOne());
    }

    @Override
    public Optional<DocumentNumberGenerator> getByDocumentType(DocumentType documentType, List<Long> companyIds) {
        QDocumentNumberGenerator qDocumentNumberGenerator = QDocumentNumberGenerator.documentNumberGenerator;

        return Optional.ofNullable(this.query
                .selectFrom(qDocumentNumberGenerator)
                .where(qDocumentNumberGenerator.type.eq(documentType))
                .where(qDocumentNumberGenerator.company.id.in(companyIds))
                .fetchOne());
    }

    private Long total() {
        QDocumentNumberGenerator qDocumentNumberGenerator = QDocumentNumberGenerator.documentNumberGenerator;

        return query.selectFrom(qDocumentNumberGenerator).fetchCount();
    }
}
