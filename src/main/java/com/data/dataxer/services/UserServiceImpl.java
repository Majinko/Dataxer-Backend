package com.data.dataxer.services;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.repositories.AppUserRepository;
import com.data.dataxer.repositories.qrepositories.QAppUserRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import com.data.dataxer.utils.StringUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final FirebaseAuth firebaseAuth;
    private final AppUserRepository userRepository;
    private final QAppUserRepository qAppUserRepository;

    public UserServiceImpl(FirebaseAuth firebaseAuth, AppUserRepository userRepository, QAppUserRepository qAppUserRepository) {
        this.firebaseAuth = firebaseAuth;
        this.userRepository = userRepository;
        this.qAppUserRepository = qAppUserRepository;
    }

    @Override
    public AppUser loggedUser() {
        return this.userRepository.findById(SecurityUtils.id()).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    // todo create camunda process
    public AppUser store(AppUser appUser) {
        appUser.setUid(this.createFirebaseUser(appUser));

        return this.userRepository.save(appUser);
    }

    // create user also for firebase
    private String createFirebaseUser(AppUser appUser) {
        UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest();

        createRequest.setEmail(appUser.getEmail());
        createRequest.setPassword(StringUtils.generateRandomTextPassword());
        createRequest.setDisplayName(appUser.getFirstName() + " " + appUser.getLastName());

        UserRecord userRecord;

        try {
            userRecord = firebaseAuth.createUser(createRequest);
            return userRecord.getUid();
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("User with email " + appUser.getEmail() + ", is already registered");
        }
    }

    @Override
    public List<AppUser> all() {
        return this.userRepository.getAll(SecurityUtils.companyId());
    }

    @Override
    public AppUser update(AppUser appUser) {
        return this.userRepository.findByUid(SecurityUtils.uid()).map(user -> {

            user.setFirstName(appUser.getFirstName());
            user.setLastName(appUser.getLastName());
            user.setPhone(appUser.getPhone());
            user.setStreet(appUser.getStreet());
            user.setCity(appUser.getCity());
            user.setPostalCode(appUser.getPostalCode());
            user.setCountry(appUser.getCountry());

            return userRepository.save(user);
        }).orElse(null);
    }
}
