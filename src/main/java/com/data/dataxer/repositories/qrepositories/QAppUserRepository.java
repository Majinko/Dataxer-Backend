package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.AppUser;

import java.util.List;

public interface QAppUserRepository {
    List<AppUser> all(List<Long> companyIds);
}
