package com.data.dataxer.repositories;

import com.data.dataxer.security.model.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RoleRepository extends CrudRepository<Role, Long> {
    List<Role> findAllByCompanyId(Long companyId);
}
