package com.data.dataxer.securityContextUtils;

import com.data.dataxer.models.domain.Company;
import com.data.dataxer.models.domain.User;
import com.data.dataxer.security.model.UserAuthenticationDetails;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

public class SecurityContextUtils {
    public static Long id() {
        return ((UserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getId();
    }

    public static Long CompanyId() {
        return ((UserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getCompanies().get(0).getId();
    }

    public static User user() {
        return ((UserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }

    public static List<Long> CompanyIds() {
        return ((UserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getCompanyIds();
    }
}
