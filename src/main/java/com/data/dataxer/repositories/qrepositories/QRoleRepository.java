package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.security.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QRoleRepository {
    Page<Role> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long companyId);
}
