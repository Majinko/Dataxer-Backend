package com.data.dataxer.services;

import com.data.dataxer.security.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleService {
    List<Role> getAll();

    void storeOrUpdate(Role role);

    void destroy(Long id);

    Role getById(Long id);

    Page<Role> paginate(Pageable pageable, String rqlFilter, String sortExpression);
}
