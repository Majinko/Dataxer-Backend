package com.data.dataxer.services;
import com.data.dataxer.models.domain.DataxerUser;
import com.data.dataxer.repositories.UserRepository;
import com.data.dataxer.securityContextUtils.SecurityContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public DataxerUser loggedUser() {
        return this.userRepository.findById(SecurityContextUtils.id()).orElseThrow(() -> new RuntimeException("Contact not found"));
    }
}
