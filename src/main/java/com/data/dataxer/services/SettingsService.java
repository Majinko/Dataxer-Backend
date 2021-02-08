package com.data.dataxer.services;

import com.data.dataxer.models.domain.Settings;

import java.util.List;

public interface SettingsService {

    void makeSettingsForCompany(Long id);

    List<Settings> getCompanySettings(Long id);

    Settings getByName(String name, Boolean disableFilter);

    void destroyAllCompanySettings(Long companyId);

    void regenerateCompanySettings(Long companyId);
}

