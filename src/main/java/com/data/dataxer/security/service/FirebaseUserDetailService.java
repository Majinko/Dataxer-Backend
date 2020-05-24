package com.data.dataxer.security.service;

import com.data.dataxer.models.domain.DataxerUser;
import com.data.dataxer.repositories.CompanyRepository;
import com.data.dataxer.repositories.DataxerUserRepository;
import com.data.dataxer.security.model.FirebaseUserAuthenticationDetails;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class FirebaseUserDetailService {
    private final DataxerUserRepository userRepository;
    private final CompanyRepository companyRepository;

    public FirebaseUserDetailService(DataxerUserRepository userRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    public UserDetails loadOrCreateUser(FirebaseToken firebaseToken) {
        DataxerUser user = userRepository.findByUid(firebaseToken.getUid()).orElseGet(() -> userRepository.save(createUser(firebaseToken)));

        return new FirebaseUserAuthenticationDetails(user, companyRepository);
    }

    DataxerUser createUser(FirebaseToken token) {
        DataxerUser user = new DataxerUser();

        user.setUid(token.getUid());
        user.setEmail(token.getEmail());

        return user;
    }
}
