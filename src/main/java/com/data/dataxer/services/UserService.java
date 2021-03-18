package com.data.dataxer.services;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.dto.AppUserOverviewDTO;

import java.util.List;

public interface UserService {
    AppUser loggedUser();

    AppUser store(AppUser appUser);

    List<AppUser> all();

    List<AppUserOverviewDTO> overview();

    AppUser update(AppUser appUserDTOtoAppUser);

    AppUser getByUid(String uid);

    AppUser getByIdAndUid(Long id, String uid);

    void destroy(String uid);
}
