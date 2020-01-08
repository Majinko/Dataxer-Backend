package com.data.dataxer.services;

import com.data.dataxer.models.domain.DataxerUser;
import com.data.dataxer.models.dto.TokenDTO;

public interface AuthService {
    void signUp(DataxerUser user);

    TokenDTO auth(DataxerUser user);
}
