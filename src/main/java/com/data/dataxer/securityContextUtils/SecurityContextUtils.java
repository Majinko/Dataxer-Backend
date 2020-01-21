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
    public static Long id() {
        return ((FirebaseUserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getId();
    }

    public static DataxerUser loggedUser() {
        return ((FirebaseUserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getLoggedUser();
    }

    public static List<Long> CompanyIds() {
        return ((FirebaseUserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getCompanyIds();
    }

    public static Company defaultCompany(){
        return SecurityContextUtils.loggedUser().getCompanies().get(0);
    }
}
