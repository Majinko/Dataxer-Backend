package com.data.dataxer.services;

import com.data.dataxer.models.domain.DataxerUser;

public interface UserService {
    DataxerUser loggedUser();

    DataxerUser store(DataxerUser dataxerUser);
}
