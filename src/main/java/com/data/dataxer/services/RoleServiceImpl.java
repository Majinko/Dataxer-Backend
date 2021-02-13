package com.data.dataxer.services;

import com.data.dataxer.repositories.RoleRepository;
import com.data.dataxer.security.model.Role;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    RoleRepository roleRepository;

    @Override
    public List<Role> getAll() {
        return this.roleRepository.findAllByCompanyId(SecurityUtils.companyId());
    }
}
