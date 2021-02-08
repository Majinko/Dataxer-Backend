package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.MailAccounts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QMailAccountsRepository {

    Optional<MailAccounts> getById(Long id, Long companyId, Boolean disableFilter);

    long updateByMailAccounts(MailAccounts mailAccounts, Long companyId, Boolean disableFilter);

    Page<MailAccounts> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long companyId, Boolean disableFilter);

    Optional<MailAccounts> getByCompaniesId(Long companyId);
}
