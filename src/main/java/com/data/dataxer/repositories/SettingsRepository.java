package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Settings;
import org.springframework.data.repository.CrudRepository;

public interface SettingsRepository extends CrudRepository<Settings, Long> {
}
