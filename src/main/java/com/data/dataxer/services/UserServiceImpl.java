package com.data.dataxer.services;
import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.repositories.AppUserRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final AppUserRepository userRepository;

    public UserServiceImpl(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AppUser loggedUser() {
        return this.userRepository.findById(SecurityUtils.id()).orElseThrow(() -> new RuntimeException("Contact not found"));
    }

    @Override
    public AppUser store(AppUser appUser) {
        return this.userRepository.save(appUser);
    }
}
