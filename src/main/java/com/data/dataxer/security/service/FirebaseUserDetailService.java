package com.data.dataxer.security.service;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.repositories.CompanyRepository;
import com.data.dataxer.repositories.AppUserRepository;
import com.data.dataxer.security.model.FirebaseUserAuthenticationDetails;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class FirebaseUserDetailService {
    private final AppUserRepository userRepository;
    private final CompanyRepository companyRepository;

    public FirebaseUserDetailService(AppUserRepository userRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    public UserDetails loadOrCreateUser(FirebaseToken firebaseToken) {
        AppUser user = userRepository.findByUid(firebaseToken.getUid()).orElseGet(() -> userRepository.save(createUser(firebaseToken)));

        return new FirebaseUserAuthenticationDetails(user, companyRepository);
    }

    AppUser createUser(FirebaseToken token) {
        AppUser user = new AppUser();

        user.setUid(token.getUid());
        user.setEmail(token.getEmail());

        return user;
    }
}
