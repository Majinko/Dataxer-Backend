package com.data.dataxer.security;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.repositories.CompanyRepository;
import com.data.dataxer.repositories.PrivilegeRepository;
import com.data.dataxer.repositories.RoleRepository;
import com.data.dataxer.security.model.Privilege;
import com.data.dataxer.security.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class RoleConfig {
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private CompanyRepository companyRepository;

    //@PostConstruct
    public void init() {
        List<Privilege> adminPrivileges = this.initAdminPrivileges();

        initializeRole(ROLE_ADMIN, adminPrivileges);
    }

    private List<Privilege> initAdminPrivileges() {
        List<String> privileges = new ArrayList<>();
        List<Privilege> response = new ArrayList<>();

        privileges.add(Company.class.getSimpleName());
        privileges.add(Contact.class.getSimpleName());
        privileges.add(Cost.class.getSimpleName());
        privileges.add(Demand.class.getSimpleName());
        privileges.add(Item.class.getSimpleName());
        privileges.add(Pack.class.getSimpleName());
        privileges.add("Document");
        privileges.add(Project.class.getSimpleName());
        privileges.add(Settings.class.getSimpleName());
        privileges.add(Task.class.getSimpleName());
        privileges.add(Time.class.getSimpleName());
        privileges.add(Category.class.getSimpleName());
        privileges.add("Overview");

        List<String> existedPrivileges = this.privilegeRepository.findAllPrivileges();
        privileges.removeAll(existedPrivileges);

        privileges.forEach(privilegeName -> {
            Privilege privilege = new Privilege(privilegeName);
            response.add(this.privilegeRepository.save(privilege));
        });

        return response;
    }

    private void initializeRole(String role, List<Privilege> privileges) {
        Role newRole = this.roleRepository.findRoleByName(role).orElse(null);

        if (newRole == null) {
            newRole = new Role();
            newRole.setName(role);
            newRole.setPrivileges(privileges);
            newRole.setCompany(this.companyRepository.findById(1L).orElse(null));
            this.roleRepository.save(newRole);
        }
    }
}
