package com.data.dataxer.repositories;

import com.data.dataxer.security.model.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends CrudRepository<Role, Long> {
    @Query("SELECT DISTINCT r from  Role r left join fetch r.privileges where r.appProfile.id in ?1 ")
    List<Role> findAllByAppProfileId(Long appProfileId);

    Optional<Role> findRoleByName(String name);
}
