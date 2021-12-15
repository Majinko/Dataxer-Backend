package com.data.dataxer.services;

import com.data.dataxer.mappers.RoleMapper;
import com.data.dataxer.mappers.SalaryMapper;
import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.Company;
import com.data.dataxer.models.dto.AppUserOverviewDTO;
import com.data.dataxer.repositories.AppUserRepository;
import com.data.dataxer.repositories.CompanyRepository;
import com.data.dataxer.repositories.SalaryRepository;
import com.data.dataxer.repositories.qrepositories.QAppUserRepository;
import com.data.dataxer.repositories.qrepositories.QTimeRepository;
import com.data.dataxer.security.model.Role;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import com.data.dataxer.utils.EmailUtils;
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
import java.util.Arrays;
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
    private QAppUserRepository qAppUserRepository;
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

    @Autowired
    MailAccountsServiceImpl mailAccountsService;


    @Override
    public AppUser loggedUser() {
        return SecurityUtils.loggedUser();
    }

    // todo create camunda process
    @Override
    public AppUser store(AppUser appUser) {
        AppUser user = this.userRepository.findUserByEmail(appUser.getEmail()).orElse(this.storeUserAndSendRegistrationEmail(appUser));

        this.addCompanyToUser(SecurityUtils.companyId(), user);

        return user;
    }

    private AppUser storeUserAndSendRegistrationEmail(AppUser appUser) {
        String pass = StringUtils.generateRandomTextPassword();
        UserRecord userRecord = this.createFirebaseUser(appUser, pass);

        appUser.setUid(userRecord.getUid());
        appUser.setDefaultCompany(SecurityUtils.defaultCompany());

        this.mailAccountsService.sendEmail(EmailUtils.newUser(appUser.getEmail(), pass), List.of(appUser.getEmail()));

        return this.userRepository.save(appUser);
    }

    private UserRecord createFirebaseUser(AppUser appUser, String password) {
        UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest();

        createRequest.setEmail(appUser.getEmail());
        createRequest.setPassword(password);
        createRequest.setDisplayName(appUser.getFirstName() + " " + appUser.getLastName());

        UserRecord userRecord;

        try {
            userRecord = firebaseAuth.createUser(createRequest);
            return userRecord;
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
    public Page<AppUserOverviewDTO> overview(Pageable pageable, String qString) {
        List<AppUserOverviewDTO> appUserOverviewDTOS = new ArrayList<>();
        List<AppUser> appUsers = this.qAppUserRepository.getUsersByCompany(pageable, qString, SecurityUtils.companyIds());

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
            appUserOverviewDTO.setSalary(salaryMapper.salaryToSalaryDTO(salaryRepository.findByUserUidAndFinishIsNullAndCompanyId(user.getUid(), SecurityUtils.companyId())));
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
        AppUser appUser = this.userRepository.findByUid(uid);

        if (!appUser.getRoles().isEmpty()) {
            appUser.getRoles().forEach(r -> {
                r.setPrivileges(null);
            });
        }

        return appUser;
    }

    @Override
    public AppUserOverviewDTO userOverview(String uid) {
        return this.fillAppUserOverview(this.userWithRoles(uid), true);
    }

    @Override
    public AppUser connect(String uid) {
        AppUser appUser = this.getByUid(uid);

        appUser.setConnected(true);

        userRepository.save(appUser);

        return appUser;
    }

    @Override
    public AppUser disconnect(String uid) {
        AppUser appUser = this.getByUid(uid);

        appUser.setConnected(false);

        userRepository.save(appUser);

        return appUser;
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
