package com.data.dataxer.repositories;

import com.data.dataxer.security.model.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends CrudRepository<Role, Long> {
    @Query("SELECT DISTINCT r from  Role r left join fetch r.privileges where r.company.id in ?1 ")
    List<Role> findAllByCompanyIdIn(List<Long> companyIds);

    @Query("SELECT r from  Role r join fetch r.privileges where r.company.id = ?1")
    Set<Role> findAllByCompanyIdSet(Long companyId);

    Optional<Role> findRoleByName(String name);

    @Query("SELECT r FROM Role r WHERE r.id = ?1 AND r.company.id = ?2")
    Optional<Role> findRoleByIdAndAndCompanyId(Long id, Long companyId);
}
