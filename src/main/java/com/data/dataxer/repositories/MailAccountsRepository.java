package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.MailAccounts;
import org.springframework.data.repository.CrudRepository;

public interface MailAccountsRepository extends CrudRepository<MailAccounts, Long> {
}
