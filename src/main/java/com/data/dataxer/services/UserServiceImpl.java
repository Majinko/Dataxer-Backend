package com.data.dataxer.services;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.repositories.AppUserRepository;
import com.data.dataxer.repositories.qrepositories.QAppUserRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final AppUserRepository userRepository;
    private final QAppUserRepository qAppUserRepository;

    public UserServiceImpl(AppUserRepository userRepository, QAppUserRepository qAppUserRepository) {
        this.userRepository = userRepository;
        this.qAppUserRepository = qAppUserRepository;
    }

    @Override
    public AppUser loggedUser() {
        return this.userRepository.findById(SecurityUtils.id()).orElseThrow(() -> new RuntimeException("Contact not found"));
    }

    @Override
    public AppUser store(AppUser appUser) {
        return this.userRepository.save(appUser);
    }

    @Override
    public List<AppUser> all() {
        return this.qAppUserRepository.all(SecurityUtils.companyIds());
    }
}
