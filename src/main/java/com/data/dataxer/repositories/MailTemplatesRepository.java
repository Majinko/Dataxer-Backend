package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.MailTemplate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MailTemplatesRepository extends CrudRepository<MailTemplate, Long> {
    @Query("SELECT mt FROM MailTemplate mt where mt.company.id = ?1")
    List<MailTemplate> findAllByCompanyId(Long companyId);
}
