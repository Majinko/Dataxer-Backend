package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.QSettings;
import com.data.dataxer.models.domain.Settings;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class QSettingsRepositoryImpl implements QSettingsRepository {

    private final JPAQueryFactory query;

    public QSettingsRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Settings> paginate(Pageable pageable, Filter filter, List<Long> companyIds) {
        QSettings qSettings = QSettings.settings;

        List<Settings> allSettings = this.query.selectFrom(qSettings)
                .where(qSettings.company.id.in(companyIds))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(qSettings.id.desc())
                .fetch();


        return new PageImpl<Settings>(allSettings, pageable, total(companyIds));
    }

    @Override
    public Optional<Settings> getById(Long id, List<Long> companyIds) {
        QSettings qSettings = QSettings.settings;

        return Optional.ofNullable(
            this.query.selectFrom(qSettings)
                .where(qSettings.id.eq(id))
                .where(qSettings.company.id.in(companyIds))
                .fetchOne()
        );
    }

    @Override
    public Optional<Settings> getByName(String name, List<Long> companyIds) {
        QSettings qSettings = QSettings.settings;

        return Optional.ofNullable(
                this.query.selectFrom(qSettings)
                        .where(qSettings.name.eq(name))
                        .where(qSettings.company.id.in(companyIds))
                        .fetchOne()
        );
    }

    private long total(List<Long> companyIds) {
        QSettings qSettings = QSettings.settings;

        return this.query.selectFrom(qSettings)
                .where(qSettings.company.id.in(companyIds))
                .fetchCount();
    }
}
