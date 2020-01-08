package com.data.dataxer.security.service;

import com.data.dataxer.models.domain.DataxerUser;
import com.data.dataxer.repositories.UserRepository;
import com.data.dataxer.security.model.FirebaseUserAuthenticationDetails;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class FirebaseUserDetailService {
    private final UserRepository userRepository;

    public FirebaseUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDetails loadOrCreateUser(FirebaseToken firebaseToken) {
        DataxerUser user = userRepository.findByUid(firebaseToken.getUid()).orElseThrow(() -> new RuntimeException("user not found"));

        return new FirebaseUserAuthenticationDetails(user);
    }
}
