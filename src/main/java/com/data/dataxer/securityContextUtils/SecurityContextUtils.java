package com.data.dataxer.securityContextUtils;

import com.data.dataxer.models.domain.User;
import com.data.dataxer.security.model.UserAuthenticationDetails;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextUtils {
    public static Long id() {
        return ((UserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getId();
    }

    public static Long CompanyId() {
        if (((UserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser() != null) {
            return ((UserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getCompany().getId();
        }

        return null;
    }

    public static User user() {
        if (((UserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser() != null) {
            return ((UserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        }

        return null;
    }
}
