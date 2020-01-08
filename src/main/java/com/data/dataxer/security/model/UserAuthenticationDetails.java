package com.data.dataxer.security.model;

import com.data.dataxer.models.domain.Company;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UserAuthenticationDetails extends User {
    private com.data.dataxer.models.domain.User user;
    private List<Long> companyIds = new ArrayList<Long>();

    public UserAuthenticationDetails(com.data.dataxer.models.domain.User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getEmail(), user.getPassword(), authorities);

        this.user = user;

        if (!user.getCompanies().isEmpty()) {
            this.companyIds = this.user.getCompanies().stream().map(Company::getId).collect(Collectors.toList());
        }
    }
}
