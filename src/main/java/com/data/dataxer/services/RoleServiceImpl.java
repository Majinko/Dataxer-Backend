package com.data.dataxer.services;

import com.data.dataxer.repositories.PrivilegeRepository;
import com.data.dataxer.repositories.RoleRepository;
import com.data.dataxer.security.model.Privilege;
import com.data.dataxer.security.model.Role;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;

    public RoleServiceImpl(RoleRepository roleRepository, PrivilegeRepository privilegeRepository) {
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
    }

    @Override
    public List<Role> getAll() {
        return this.roleRepository.findAllByCompanyId(SecurityUtils.companyId());
    }

    @Override
    public void storeOrUpdate(Role role) {
        List<String> privilegeNames = role.getPrivileges().stream().map(Privilege::getName).collect(Collectors.toList());
        List<Privilege> privileges = this.loadAndCheckPrivileges(privilegeNames);

        role.setPrivileges(privileges);
        this.roleRepository.save(role);
    }

    private List<Privilege> loadAndCheckPrivileges(List<String> privilegeNames) {
        List<Privilege> privileges = this.privilegeRepository.findAllByNameIn(privilegeNames);

        if (privilegeNames.size() != privileges.size()) {
            throw new RuntimeException("Trying to assign not existed privilege");
        }

        return privileges;
    }
}
