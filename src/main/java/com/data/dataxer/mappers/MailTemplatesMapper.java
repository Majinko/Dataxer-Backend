package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.MailTemplates;
import com.data.dataxer.models.dto.MailTemplatesDTO;
import org.mapstruct.Mapper;

@Mapper
public interface MailTemplatesMapper {
    MailTemplates mailTemplatesDTOToMailTemplates(MailTemplatesDTO mailTemplatesDTO);

    MailTemplatesDTO mailTemplatesToMailTemplatesDTO(MailTemplates mailTemplates);
}
