package com.data.dataxer.securityContextUtils;

import com.data.dataxer.models.domain.DataxerUser;
import com.data.dataxer.security.model.FirebaseUserAuthenticationDetails;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public class SecurityContextUtils {
    public static Long id() {
        return ((FirebaseUserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getId();
    }

    public static Long CompanyId() {
        return ((FirebaseUserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getCompanies().get(0).getId();
    }

    public static DataxerUser loggedUser() {
        return ((FirebaseUserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getLoggedUser();
    }

    public static List<Long> CompanyIds() {
        return ((FirebaseUserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getCompanyIds();
    }
}
