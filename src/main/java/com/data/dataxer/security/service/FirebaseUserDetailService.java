package com.data.dataxer.security.service;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.repositories.AppProfileRepository;
import com.data.dataxer.repositories.CompanyRepository;
import com.data.dataxer.repositories.qrepositories.QAppUserRepository;
import com.data.dataxer.security.model.FirebaseUserAuthenticationDetails;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class FirebaseUserDetailService {
    private final QAppUserRepository qAppUserRepository;
    private final AppProfileRepository appProfileRepository;

    public FirebaseUserDetailService(AppProfileRepository appProfileRepository, QAppUserRepository qAppUserRepository) {
        this.qAppUserRepository = qAppUserRepository;
        this.appProfileRepository = appProfileRepository;
    }

    public UserDetails loadOrCreateUser(FirebaseToken firebaseToken) {
        AppUser user = this.qAppUserRepository.findByUid(firebaseToken.getUid()).orElseThrow(() -> new RuntimeException("User not exist :("));

        return new FirebaseUserAuthenticationDetails(user, appProfileRepository);
    }
}
