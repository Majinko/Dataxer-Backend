package com.data.dataxer.security;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.repositories.PrivilegeRepository;
import com.data.dataxer.repositories.RoleRepository;
import com.data.dataxer.security.model.Privilege;
import com.data.dataxer.security.model.Role;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RoleConfig {

    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;

    public RoleConfig(RoleRepository roleRepository, PrivilegeRepository privilegeRepository) {
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
    }


    public void init() {
        List<Privilege> readPrivileges = this.initializeReadPrivileges();
        List<Privilege> writePrivileges = this.initializeWritePrivileges();

        initializeRole(ROLE_ADMIN, Stream.concat(readPrivileges.stream(), writePrivileges.stream()).collect(Collectors.toList()));
    }

    private List<Privilege> initializeReadPrivileges() {
        List<String> privileges = new ArrayList<>();
        List<Privilege> response = new ArrayList<>();

        privileges.add(makeReadPrivilegeName(BankAccount.class.getSimpleName()));
        privileges.add(makeReadPrivilegeName(BillingInformation.class.getSimpleName()));
        privileges.add(makeReadPrivilegeName(Category.class.getSimpleName()));
        privileges.add(makeReadPrivilegeName(Company.class.getSimpleName()));
        privileges.add(makeReadPrivilegeName(Contact.class.getSimpleName()));
        privileges.add(makeReadPrivilegeName(Cost.class.getSimpleName()));
        privileges.add(makeReadPrivilegeName(Demand.class.getSimpleName()));
        privileges.add(makeReadPrivilegeName(DocumentNumberGenerator.class.getSimpleName()));
        privileges.add(makeReadPrivilegeName(DocumentRelation.class.getSimpleName()));
        privileges.add(makeReadPrivilegeName(File.class.getSimpleName()));
        privileges.add(makeReadPrivilegeName(Invoice.class.getSimpleName()));
        privileges.add(makeReadPrivilegeName(Item.class.getSimpleName()));
        privileges.add(makeReadPrivilegeName(MailAccounts.class.getSimpleName()));
        privileges.add(makeReadPrivilegeName(MailTemplates.class.getSimpleName()));
        privileges.add(makeReadPrivilegeName(Pack.class.getSimpleName()));
        privileges.add(makeReadPrivilegeName(Payment.class.getSimpleName()));
        privileges.add(makeReadPrivilegeName(PriceOffer.class.getSimpleName()));
        privileges.add(makeReadPrivilegeName(Project.class.getSimpleName()));
        privileges.add(makeReadPrivilegeName(Role.class.getSimpleName()));
        privileges.add(makeReadPrivilegeName(Salary.class.getSimpleName()));
        privileges.add(makeReadPrivilegeName(Settings.class.getSimpleName()));
        privileges.add(makeReadPrivilegeName(Task.class.getSimpleName()));
        privileges.add(makeReadPrivilegeName(Time.class.getSimpleName()));
        privileges.add(makeReadPrivilegeName(AppUser.class.getSimpleName()));

        List<String> existedPrivileges = this.privilegeRepository.findAllReadPrivileges();
        privileges.removeAll(existedPrivileges);

        privileges.forEach(privilegeName -> {
            Privilege privilege = new Privilege(privilegeName);
            response.add(this.privilegeRepository.save(privilege));
        });

        return response;
    }

    private List<Privilege> initializeWritePrivileges() {
        List<String> privileges = new ArrayList<>();
        List<Privilege> response = new ArrayList<>();

        privileges.add(makeWritePrivilegeName(BankAccount.class.getSimpleName()));
        privileges.add(makeWritePrivilegeName(BillingInformation.class.getSimpleName()));
        privileges.add(makeWritePrivilegeName(Category.class.getSimpleName()));
        privileges.add(makeWritePrivilegeName(Company.class.getSimpleName()));
        privileges.add(makeWritePrivilegeName(Contact.class.getSimpleName()));
        privileges.add(makeWritePrivilegeName(Cost.class.getSimpleName()));
        privileges.add(makeWritePrivilegeName(Demand.class.getSimpleName()));
        privileges.add(makeWritePrivilegeName(DocumentNumberGenerator.class.getSimpleName()));
        privileges.add(makeWritePrivilegeName(DocumentRelation.class.getSimpleName()));
        privileges.add(makeWritePrivilegeName(File.class.getSimpleName()));
        privileges.add(makeWritePrivilegeName(Invoice.class.getSimpleName()));
        privileges.add(makeWritePrivilegeName(Item.class.getSimpleName()));
        privileges.add(makeWritePrivilegeName(MailAccounts.class.getSimpleName()));
        privileges.add(makeWritePrivilegeName(MailTemplates.class.getSimpleName()));
        privileges.add(makeWritePrivilegeName(Pack.class.getSimpleName()));
        privileges.add(makeWritePrivilegeName(Payment.class.getSimpleName()));
        privileges.add(makeWritePrivilegeName(PriceOffer.class.getSimpleName()));
        privileges.add(makeWritePrivilegeName(Project.class.getSimpleName()));
        privileges.add(makeWritePrivilegeName(Role.class.getSimpleName()));
        privileges.add(makeWritePrivilegeName(Salary.class.getSimpleName()));
        privileges.add(makeWritePrivilegeName(Settings.class.getSimpleName()));
        privileges.add(makeWritePrivilegeName(Task.class.getSimpleName()));
        privileges.add(makeWritePrivilegeName(Time.class.getSimpleName()));
        privileges.add(makeWritePrivilegeName(AppUser.class.getSimpleName()));

        List<String> existedPrivileges = this.privilegeRepository.findAllWritePrivileges();
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

    private String makeReadPrivilegeName(String className) {
        return className + "_READ";
    }

    private String makeWritePrivilegeName(String className) {
        return className + "_WRITE";
    }
}
