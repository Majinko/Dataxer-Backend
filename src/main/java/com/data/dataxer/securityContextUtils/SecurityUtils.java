package com.data.dataxer.securityContextUtils;

import com.data.dataxer.models.domain.Company;
import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.security.model.FirebaseUserAuthenticationDetails;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

public class SecurityUtils {
    public static String uid() {
        return ((FirebaseUserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getUid();
    }

    public static Long id() {
        return ((FirebaseUserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getId();
    }

    public static AppUser loggedUser() {
        return ((FirebaseUserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getLoggedUser();
    }

    public static List<Long> companyIds() {
        return ((FirebaseUserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getCompanies().stream().map(Company::getId).collect(Collectors.toList());
    }

    public static Company defaultCompany() {
        Company defCompany = null;
        List<Company> companies = ((FirebaseUserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getCompanies();
        if (!companies.isEmpty()) {
            return companies.get(0);
        }
        //return ((FirebaseUserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getCompanies().get(0);
        return null;
    }
}
