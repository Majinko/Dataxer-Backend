package com.data.dataxer.securityContextUtils;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.Company;
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
        return ((FirebaseUserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getCompanies().stream().map(Company::getId).collect(Collectors.toList());
    }

    public static List<Long> companyIds(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            boolean isCompanyIdsOk = ids.stream().anyMatch(id -> SecurityUtils.companyIds().contains(id)); // check company ids is in SecurityUtils.companyIds();

            if (!isCompanyIdsOk) {
                throw new RuntimeException("wrong company ids :(");
            }

            return ids;
        }

        return SecurityUtils.companyIds();
    }

    public static Company defaultCompany() {
        return ((FirebaseUserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getLoggedUser().getDefaultCompany();
    }

    public static Long companyId() {
        return defaultCompany().getId();
    }

    public static Long companyId(Long checkIsInUserCompanies) {
        if (!SecurityUtils.companyIds().contains(checkIsInUserCompanies)) {
            throw new RuntimeException("you are wrong, no no no");
        }

        return defaultCompany().getId();
    }
}
