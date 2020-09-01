package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.Settings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QSettingsRepository {

    Page<Settings> paginate(Pageable pageable, Filter filter, List<Long> companyIds);

    Optional<Settings> getById(Long id, List<Long> companyIds);

    Optional<Settings> getByName(String name, List<Long> companyIds);

}
