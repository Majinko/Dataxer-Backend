package com.data.dataxer.security.model;

import com.data.dataxer.models.domain.Company;
import com.data.dataxer.models.domain.DataxerUser;
import lombok.Getter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class FirebaseUserAuthenticationDetails implements UserDetails {
    private DataxerUser user;
    private List<Long> companyIds = new ArrayList<Long>();

    public FirebaseUserAuthenticationDetails(DataxerUser user) {
        this.user = user;

        if (!user.getCompanies().isEmpty()) {
            this.companyIds = this.user.getCompanies().stream().map(Company::getId).collect(Collectors.toList());
        }
    }

    public DataxerUser getLoggedUser() {
        return this.user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
