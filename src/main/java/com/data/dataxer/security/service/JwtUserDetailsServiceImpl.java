package com.data.dataxer.security.service;

import com.data.dataxer.models.domain.User;
import com.data.dataxer.repositories.UserRepository;
import com.data.dataxer.security.model.UserAuthenticationDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public JwtUserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = this.userRepository.findUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException(String.format("User with email %s was not found.", email)));

       /* return new UserAuthenticationDetails(user, user.getRoles().stream().map(role ->
                new SimpleGrantedAuthority(role.getName())
        ).collect(Collectors.toList()));*/

        return new UserAuthenticationDetails(user, createAccountAuthorities());
    }

    // TODO add more ROLES
    private static List<GrantedAuthority> createAccountAuthorities() {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return authorities;
    }
}
