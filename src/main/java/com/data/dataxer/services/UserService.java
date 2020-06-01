package com.data.dataxer.services;

import com.data.dataxer.models.domain.AppUser;

public interface UserService {
    AppUser loggedUser();

    AppUser store(AppUser appUser);
}
