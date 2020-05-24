package com.data.dataxer.services;
import com.data.dataxer.models.domain.DataxerUser;
import com.data.dataxer.repositories.DataxerUserRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final DataxerUserRepository userRepository;

    public UserServiceImpl(DataxerUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public DataxerUser loggedUser() {
        return this.userRepository.findById(SecurityUtils.id()).orElseThrow(() -> new RuntimeException("Contact not found"));
    }

    @Override
    public DataxerUser store(DataxerUser dataxerUser) {
        return this.userRepository.save(dataxerUser);
    }
}
