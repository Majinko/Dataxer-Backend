package com.data.dataxer.services;

import com.data.dataxer.models.domain.AppUser;

import java.util.List;

public interface UserService {
    AppUser loggedUser();

    AppUser store(AppUser appUser);

    List<AppUser> all();

    AppUser update(AppUser appUserDTOtoAppUser);
}
