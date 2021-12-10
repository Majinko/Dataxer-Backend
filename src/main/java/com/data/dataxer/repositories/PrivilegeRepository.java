package com.data.dataxer.repositories;

import com.data.dataxer.security.model.Privilege;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PrivilegeRepository extends CrudRepository<Privilege, Long> {

    @Query("SELECT p.name FROM Privilege p")
    List<String> findAllPrivileges();

    List<Privilege> findAllByNameIn(List<String> names);

    @Query("SELECT p FROM Privilege p")
    List<Privilege> getAllPrivilege();

}