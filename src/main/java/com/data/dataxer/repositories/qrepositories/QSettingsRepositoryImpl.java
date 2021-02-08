package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.QSettings;
import com.data.dataxer.models.domain.Settings;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class QSettingsRepositoryImpl implements QSettingsRepository {

    private final JPAQueryFactory query;
    private final EntityManager entityManager;

    public QSettingsRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager.getEntityManagerFactory().createEntityManager();
        this.query = new JPAQueryFactory(this.entityManager);
    }

    @Override
    public Optional<Settings> getByName(String name, Long companyId, Boolean disableFilter) {
        QSettings qSettings = QSettings.settings;

        if (!disableFilter) {
            this.entityManager.unwrap(Session.class).enableFilter("companyCondition").setParameter("companyId", companyId);
        }

        return Optional.ofNullable(
                this.query.selectFrom(qSettings)
                        .where(qSettings.name.eq(name))
                        .fetchOne()
        );
    }

    @Override
    public List<Settings> getByCompanyId(Long companyId) {
        QSettings qSettings = QSettings.settings;

        return this.query.selectFrom(qSettings)
                .where(qSettings.company.id.eq(companyId))
                .fetch();
    }

    @Override
    public void deleteAllSettingsByCompany(Long companyId) {
        QSettings qSettings = QSettings.settings;

        this.query.delete(qSettings)
                .where(qSettings.company.id.eq(companyId))
                .execute();
    }
}
