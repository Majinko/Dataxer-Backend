package com.data.dataxer.services;

import com.data.dataxer.models.domain.AppProfile;
import com.data.dataxer.repositories.AppProfileRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppProfileServiceImpl implements AppProfileService {
    @Autowired
    AppProfileRepository appProfileRepository;

    @Override
    public AppProfile getDefaultUserAppProfile() {
        return SecurityUtils.defaultProfile();
    }
}
