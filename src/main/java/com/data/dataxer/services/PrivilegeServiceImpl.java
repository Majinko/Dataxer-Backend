package com.data.dataxer.services;

import com.data.dataxer.repositories.PrivilegeRepository;
import com.data.dataxer.security.model.Privilege;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrivilegeServiceImpl implements PrivilegeService{

    private final PrivilegeRepository privilegeRepository;

    public PrivilegeServiceImpl(PrivilegeRepository privilegeRepository) {
        this.privilegeRepository = privilegeRepository;
    }

    @Override
    public List<Privilege> getAll() {
        return this.privilegeRepository.getAllPrivilege();
    }
}
