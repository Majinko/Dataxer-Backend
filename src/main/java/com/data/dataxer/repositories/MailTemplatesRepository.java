package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.MailTemplate;
import com.data.dataxer.models.enums.MailTemplateType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MailTemplatesRepository extends CrudRepository<MailTemplate, Long> {
    @Query("SELECT mt FROM MailTemplate mt where mt.appProfile.id = ?1")
    List<MailTemplate> findAllByAppProfileId(Long appProfileId);

    @Query("SELECT mt from  MailTemplate  mt where mt.mailTemplateType = ?1 and mt.appProfile.id = ?2")
    MailTemplate findByType(MailTemplateType type, Long appProfileId);
}
