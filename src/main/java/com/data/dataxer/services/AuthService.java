package com.data.dataxer.services;

import com.data.dataxer.models.domain.User;
import com.data.dataxer.models.dto.TokenDTO;

public interface AuthService {
    void signUp(User user);

    TokenDTO auth(User user);
}
