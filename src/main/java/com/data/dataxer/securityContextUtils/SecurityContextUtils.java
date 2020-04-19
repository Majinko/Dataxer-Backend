package com.data.dataxer.securityContextUtils;

import com.data.dataxer.models.domain.Company;
import com.data.dataxer.models.domain.DataxerUser;
import com.data.dataxer.security.model.FirebaseUserAuthenticationDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

public class SecurityContextUtils {
    public static String uid() {
        return ((FirebaseUserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getUid();
    }

    public static Long id() {
        return ((FirebaseUserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getId();
    }

    public static DataxerUser loggedUser() {
        return ((FirebaseUserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getLoggedUser();
    }

    public static List<Long> companyIds() {
        return ((FirebaseUserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getCompanies().stream().map(Company::getId).collect(Collectors.toList());
    }

    public static Company defaultCompany() {
        return ((FirebaseUserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getCompanies().get(0);
    }
}
