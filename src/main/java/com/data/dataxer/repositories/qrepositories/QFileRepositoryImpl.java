package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.File;
import com.data.dataxer.models.domain.QFile;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class QFileRepositoryImpl implements QFileRepository{

    private final JPAQueryFactory query;

    public QFileRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<File> getByName(String fileName, List<Long> companyIds) {
        QFile qFile = QFile.file;

        return Optional.ofNullable(this.query.selectFrom(qFile)
                .where(qFile.name.eq(fileName))
                .where(qFile.company.id.in(companyIds))
                .fetchOne());
    }

    @Override
    public Page<File> paginate(Pageable pageable, List<Long> companyIds) {
        QFile qFile = QFile.file;

        List<File> files = query.selectFrom(qFile)
                .where(qFile.company.id.in(companyIds))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(qFile.id.desc())
                .fetch();
        return new PageImpl<File>(files, pageable, total());
    }

    private Long total() {
        QFile qFile = QFile.file;

        return this.query.selectFrom(qFile).fetchCount();
    }

}
