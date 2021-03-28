package com.data.dataxer.security;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.repositories.PrivilegeRepository;
import com.data.dataxer.repositories.RoleRepository;
import com.data.dataxer.security.model.Privilege;
import com.data.dataxer.security.model.Role;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class RoleConfig {

    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;

    public RoleConfig(RoleRepository roleRepository, PrivilegeRepository privilegeRepository) {
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
    }


    @PostConstruct
    public void init() {
        List<Privilege> privileges = this.initializePrivileges();

        initializeRole(ROLE_ADMIN, privileges);
    }

    private List<Privilege> initializePrivileges() {
        List<String> privileges = new ArrayList<>();
        List<Privilege> response = new ArrayList<>();

        privileges.add(BankAccount.class.getSimpleName());
        privileges.add(Category.class.getSimpleName());
        privileges.add(Company.class.getSimpleName());
        privileges.add(Contact.class.getSimpleName());
        privileges.add(Cost.class.getSimpleName());
        privileges.add(Demand.class.getSimpleName());
        privileges.add(DocumentNumberGenerator.class.getSimpleName());
        privileges.add(DocumentRelation.class.getSimpleName());
        privileges.add(File.class.getSimpleName());
        privileges.add(Invoice.class.getSimpleName());
        privileges.add(Item.class.getSimpleName());
        privileges.add(MailAccounts.class.getSimpleName());
        privileges.add(MailTemplates.class.getSimpleName());
        privileges.add("Overview");
        privileges.add(Pack.class.getSimpleName());
        privileges.add(Payment.class.getSimpleName());
        privileges.add("Pdf");
        privileges.add(PriceOffer.class.getSimpleName());
        privileges.add(Privilege.class.getSimpleName());
        privileges.add(Project.class.getSimpleName());
        privileges.add(Role.class.getSimpleName());
        privileges.add(Settings.class.getSimpleName());
        privileges.add(Storage.class.getSimpleName());
        privileges.add(Task.class.getSimpleName());
        privileges.add(Time.class.getSimpleName());
        privileges.add(AppUser.class.getSimpleName());

        List<String> existedPrivileges = this.privilegeRepository.findAllReadPrivileges();
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
            this.roleRepository.save(newRole);
        }
    }
}
