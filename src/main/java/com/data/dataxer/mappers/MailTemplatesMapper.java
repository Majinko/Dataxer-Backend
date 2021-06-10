package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.MailTemplate;
import com.data.dataxer.models.dto.MailTemplateDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface MailTemplatesMapper {
    MailTemplate mailTemplateDTOToMailTemplates(MailTemplateDTO mailTemplatesDTO);

    MailTemplateDTO mailTemplateToMailTemplatesDTO(MailTemplate mailTemplates);

    List<MailTemplateDTO> mailTemplatesToMailTemplatesDTO(List<MailTemplate> mailTemplates);

    List<MailTemplate> mailTemplatesDTOtoMailTemplates(List<MailTemplateDTO> mailTemplates);
}
