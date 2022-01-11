package com.data.dataxer.services;

import com.data.dataxer.models.domain.AppProfile;
import com.data.dataxer.repositories.AppProfileRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppProfileServiceImpl implements AppProfileService {
    @Autowired
    AppProfileRepository appProfileRepository;

    @Override
    public AppProfile getDefaultUserAppProfile() {
        return SecurityUtils.defaultProfile();
    }

    @Override
    public List<AppProfile> getAll() {
        return this.appProfileRepository.findAllByAppUsersIn(List.of(SecurityUtils.loggedUser()));
    }

    @Override
    public void store(AppProfile appProfile) {
        this.checkCanCreateProfile(appProfile);

        this.appProfileRepository.save(appProfile);
    }

    @Override
    public void update(AppProfile appProfile) {
        this.appProfileRepository.findById(appProfile.getId()).map(profile -> {
            profile.setTitle(appProfile.getTitle());
            profile.setLogoUrl(appProfile.getLogoUrl());

            return  this.appProfileRepository.save(profile);
        }).orElse(null);
    }

    private void checkCanCreateProfile(AppProfile appProfile) {
        Optional<AppProfile> profile = this.appProfileRepository.findAllByTitle(appProfile.getTitle());

        if (profile.isPresent()) {
            throw new RuntimeException("Profile exist");
        }
    }
}
