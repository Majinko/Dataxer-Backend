package com.data.dataxer.services;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.dto.AppUserOverviewDTO;
import com.data.dataxer.security.model.Role;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    AppUser loggedUser();

    AppUser store(AppUser appUser);

    List<AppUser> all();

    AppUser update(AppUser appUserDTOtoAppUser);

    AppUser getByUid(String uid);

    AppUser getByIdAndUid(Long id, String uid);

    void destroy(String uid);

    void switchProfile(Long companyId);

    void assignRoles(String uid, List<Role> roleDTOStoRoles);

    Page<AppUserOverviewDTO> overview(Pageable pageable, String qString);

    AppUser userWithRoles(String uid);

    AppUserOverviewDTO userOverview(String uid);

    AppUser connect(String uid);

    AppUser disconnect(String uid);

    void resetToken(String uid);

    void deactivateUser(String uid);

    void activateUser(String uid);
}
