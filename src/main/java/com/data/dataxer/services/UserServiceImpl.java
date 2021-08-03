package com.data.dataxer.services;

import com.data.dataxer.mappers.RoleMapper;
import com.data.dataxer.mappers.SalaryMapper;
import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.Company;
import com.data.dataxer.models.dto.AppUserOverviewDTO;
import com.data.dataxer.repositories.AppUserRepository;
import com.data.dataxer.repositories.CompanyRepository;
import com.data.dataxer.repositories.SalaryRepository;
import com.data.dataxer.repositories.qrepositories.QTimeRepository;
import com.data.dataxer.security.model.Role;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import com.data.dataxer.utils.StringUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.data.dataxer.utils.Helpers.getDiffYears;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private FirebaseAuth firebaseAuth;
    @Autowired
    private AppUserRepository userRepository;
    @Autowired
    private QTimeRepository qTimeRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private SalaryRepository salaryRepository;
    @Autowired
    private SalaryMapper salaryMapper;
    @Autowired
    private RoleMapper roleMapper;


    @Override
    public AppUser loggedUser() {
        return SecurityUtils.loggedUser();
    }

    // todo create camunda process
    @Override
    public AppUser store(AppUser appUser) {
        appUser.setUid(this.createFirebaseUser(appUser));
        appUser.setDefaultCompany(SecurityUtils.defaultCompany());

        AppUser savedUser = this.userRepository.save(appUser);

        this.addCompanyToUser(SecurityUtils.companyId(), savedUser);

        return savedUser;
    }

    private String createFirebaseUser(AppUser appUser) {
        UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest();

        createRequest.setEmail(appUser.getEmail());
        createRequest.setPassword(StringUtils.generateRandomTextPassword());
        createRequest.setDisplayName(appUser.getFirstName() + " " + appUser.getLastName());

        UserRecord userRecord;

        try {
            userRecord = firebaseAuth.createUser(createRequest);
            return userRecord.getUid();
        } catch (FirebaseAuthException e) {
            if ("email-already-exists".equals(e.getErrorCode())) {
                throw new RuntimeException("User with email " + appUser.getEmail() + ", is already registered");
            } else {
                throw new RuntimeException("something wrong :(");
            }
        }
    }

    @Override
    public List<AppUser> all() {
        return this.userRepository.findAllByDefaultCompanyId(SecurityUtils.companyId());
    }

    @Override
    public Page<AppUserOverviewDTO> overview(Pageable pageable) {
        List<AppUser> appUsers = this.userRepository.findAllByDefaultCompanyIdOrderByIdAsc(pageable, SecurityUtils.companyId());
        List<AppUserOverviewDTO> appUserOverviewDTOS = new ArrayList<>();

        appUsers.forEach(user -> {
            appUserOverviewDTOS.add(fillAppUserOverview(user, false));
        });

        Collections.sort(appUserOverviewDTOS);

        return new PageImpl<>(appUserOverviewDTOS, pageable, this.userRepository.countAllByDefaultCompanyId(SecurityUtils.companyId()));
    }

    private AppUserOverviewDTO fillAppUserOverview(AppUser user, Boolean moreData) {
        AppUserOverviewDTO appUserOverviewDTO = new AppUserOverviewDTO();

        appUserOverviewDTO.setId(user.getId());
        appUserOverviewDTO.setUid(user.getUid());
        appUserOverviewDTO.setFullName(user.getFirstName() + ' ' + user.getLastName());
        appUserOverviewDTO.setStartWork(this.qTimeRepository.getUserFirstLastRecord(user.getId(), SecurityUtils.companyId(), false));
        appUserOverviewDTO.setYears(getDiffYears(appUserOverviewDTO.getStartWork(), this.qTimeRepository.getUserFirstLastRecord(user.getId(), SecurityUtils.companyId(), true)));
        appUserOverviewDTO.setSumTime(this.qTimeRepository.sumUserTime(user.getId(), SecurityUtils.companyId()));
        appUserOverviewDTO.setProjectCount(qTimeRepository.getCountProjects(user.getId(), SecurityUtils.companyId()));

        if (moreData) {
            appUserOverviewDTO.setRoles(roleMapper.rolesToRoleDTOs(user.getRoles()));
            appUserOverviewDTO.setSalary(salaryMapper.salaryToSalaryDTO(salaryRepository.findByUserUidAndFinishIsNull(user.getUid())));
        }

        return appUserOverviewDTO;
    }

    @Override
    public AppUser update(AppUser appUser) {
        return this.userRepository.findByUidAndDefaultCompanyId(appUser.getUid(), SecurityUtils.companyId()).map(user -> {

            user.setFirstName(appUser.getFirstName());
            user.setLastName(appUser.getLastName());
            user.setPhone(appUser.getPhone());
            user.setStreet(appUser.getStreet());
            user.setCity(appUser.getCity());
            user.setPostalCode(appUser.getPostalCode());
            user.setCountry(appUser.getCountry());
            user.setRoles(appUser.getRoles());

            return userRepository.save(user);
        }).orElse(null);
    }

    @Override
    public AppUser userWithRoles(String uid) {
        AppUser appUser = this.userRepository.findByUidAndDefaultCompanyIdWithRoles(uid, SecurityUtils.companyId());

        appUser.getRoles().forEach(r -> {
            r.setPrivileges(null);
        });

        return appUser;
    }

    @Override
    public AppUserOverviewDTO userOverview(String uid) {
        return this.fillAppUserOverview(this.userWithRoles(uid), true);
    }

    @Override
    public AppUser getByUid(String uid) {
        return this.userRepository.findByUidAndDefaultCompanyId(uid, SecurityUtils.companyId()).orElseThrow(() -> new RuntimeException("User not found :)"));
    }

    @Override
    public AppUser getByIdAndUid(Long id, String uid) {
        return this.userRepository.findByIdAndUid(id, uid).orElseThrow(() -> new RuntimeException("User not found :)"));
    }

    @Override
    public void destroy(String uid) {
        this.userRepository.delete(this.getByUid(uid));
    }

    @Override

    public void switchCompany(Long companyId) {
        AppUser user = SecurityUtils.loggedUser();

        user.setDefaultCompany(this.companyRepository.getById(companyId));

        userRepository.save(user);
    }

    public void assignRoles(String uid, List<Role> roles) {
        AppUser user = this.getByUid(uid);

        user.setRoles(roles);
        this.userRepository.save(user);
    }


    private void addCompanyToUser(Long companyId, AppUser appUser) {
        Company company = this.companyRepository.findByIdWithUsers(companyId);

        company.getAppUsers().add(appUser);
        this.companyRepository.save(company);
    }
}
