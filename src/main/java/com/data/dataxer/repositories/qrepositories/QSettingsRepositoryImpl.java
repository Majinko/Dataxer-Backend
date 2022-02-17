package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.QSettings;
import com.data.dataxer.models.domain.Settings;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
    public Optional<Settings> getByName(String name, Long apProfileId) {
        QSettings qSettings = QSettings.settings;

        return Optional.ofNullable(
                this.query.selectFrom(qSettings)
                        .where(qSettings.name.eq(name))
                        .where(qSettings.appProfile.id.eq(apProfileId))
                        .fetchOne()
        );
    }

    @Override
    public List<Settings> getByAppProfileId(Long apProfileId) {
        QSettings qSettings = QSettings.settings;

        return this.query.selectFrom(qSettings)
                .where(qSettings.appProfile.id.eq(apProfileId))
                .fetch();
    }

    @Override
    public void deleteAllSettingsByCompany(Long apProfileId) {
        QSettings qSettings = QSettings.settings;

        this.query.delete(qSettings)
                .where(qSettings.appProfile.id.eq(apProfileId))
                .execute();
    }
}
