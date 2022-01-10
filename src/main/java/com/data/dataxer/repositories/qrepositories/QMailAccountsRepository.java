package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.MailAccounts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QMailAccountsRepository {
    Optional<MailAccounts> getById(Long id, Long appProfileId);

    long updateByMailAccounts(MailAccounts mailAccounts, Long appProfileId);

    Page<MailAccounts> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long appProfileId);

    Optional<MailAccounts> getByCompaniesId(Long companyId);
}
