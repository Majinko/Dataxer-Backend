package com.data.dataxer.repositories;

import com.data.dataxer.security.model.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {
    List<Role> findAllByCompanyId(Long companyId);

    Optional<Role> findRoleByName(String name);

    @Query("SELECT r FROM Role r WHERE r.id = ?1 AND r.company.id = ?2")
    Optional<Role> findRoleByIdAndAndCompanyId(Long id, Long companyId);
}
