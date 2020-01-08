package com.data.dataxer.services;

import com.data.dataxer.models.domain.User;
import com.data.dataxer.models.dto.TokenDTO;
import com.data.dataxer.repositories.UserRepository;
import com.data.dataxer.security.jwt.JwtTokenUtils;
import com.data.dataxer.security.model.UserAuthenticationDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;

    public AuthServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, AuthenticationManager authenticationManager, JwtTokenUtils jwtTokenUtils) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Override
    public void signUp(User user) {
        if (this.userRepository.findUserByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("User Exist");
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public TokenDTO auth(User user) {
        Optional<User> userOpt = this.userRepository.findUserByEmail(user.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new TokenDTO(jwtTokenUtils.generateToken(new UserAuthenticationDetails(userOpt.orElseThrow(() -> new UsernameNotFoundException("Account was not found")), user.getRoles().stream().map(role ->
                new SimpleGrantedAuthority(role.getName())
        ).collect(Collectors.toList()))));
    }
}
