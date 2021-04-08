package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.security.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface QRoleRepository {
    Page<Role> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long companyId);

    Optional<Role> getById(Long id, Long companyId);
}
