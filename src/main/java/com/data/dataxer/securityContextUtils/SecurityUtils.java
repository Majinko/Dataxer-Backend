package com.data.dataxer.securityContextUtils;

import com.data.dataxer.models.domain.AppProfile;
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

    public static AppProfile defaultProfile() {
        return ((FirebaseUserAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getDefaultProfile();
    }

    public static Long defaultProfileId() {
        return SecurityUtils.defaultProfile().getId();
    }
}
