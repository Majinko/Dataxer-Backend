package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Settings;

import java.util.List;
import java.util.Optional;

public interface QSettingsRepository {

    Optional<Settings> getByName(String name, Long apProfileId);

    List<Settings> getByAppProfileId(Long apProfileId);

    void deleteAllSettingsByCompany(Long apProfileId);
}
