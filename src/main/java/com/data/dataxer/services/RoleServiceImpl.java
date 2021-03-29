package com.data.dataxer.services;

import com.data.dataxer.repositories.PrivilegeRepository;
import com.data.dataxer.repositories.RoleRepository;
import com.data.dataxer.repositories.qrepositories.QRoleRepository;
import com.data.dataxer.security.model.Privilege;
import com.data.dataxer.security.model.Role;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final QRoleRepository qRoleRepository;
    private final PrivilegeRepository privilegeRepository;

    public RoleServiceImpl(RoleRepository roleRepository, QRoleRepository qRoleRepository,
                           PrivilegeRepository privilegeRepository) {
        this.roleRepository = roleRepository;
        this.qRoleRepository = qRoleRepository;
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

    @Override
    public Role getById(Long id) {
        return this.qRoleRepository.getById(id, SecurityUtils.companyId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    @Override
    public Page<Role> paginate(Pageable pageable, String rqlFilter, String sortExpression) {
        return this.qRoleRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.companyId());
    }

    @Override
    public void destroy(Long id) {
        this.roleRepository.delete(this.getById(id));
    }

    private List<Privilege> loadAndCheckPrivileges(List<String> privilegeNames) {
        privilegeNames.forEach(name ->
                System.out.println(name));

        List<Privilege> privileges = this.privilegeRepository.findAllByNameIn(privilegeNames);

        if (privilegeNames.size() != privileges.size()) {
            throw new RuntimeException("Trying to assign not existed privilege");
        }

        return privileges;
    }
}
