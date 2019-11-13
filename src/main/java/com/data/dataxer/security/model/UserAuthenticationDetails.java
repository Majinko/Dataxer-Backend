package com.data.dataxer.security.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class UserAuthenticationDetails extends User {
    private com.data.dataxer.models.domain.User user;

    public UserAuthenticationDetails(com.data.dataxer.models.domain.User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getEmail(), user.getPassword(), authorities);

        this.user = user;
    }
}
