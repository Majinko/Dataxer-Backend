package com.data.dataxer.services;

import com.data.dataxer.models.domain.AppProfile;

import java.util.List;

public interface AppProfileService {
    AppProfile getDefaultUserAppProfile();

    List<AppProfile> getAll();

    void store(AppProfile appProfile);

    void update(AppProfile appProfile);
}
