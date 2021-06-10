package com.data.dataxer.models.dto;

import com.data.dataxer.models.enums.MailTemplateType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailTemplateDTO {
    private Long id;
    private String emailSubject;
    private String emailContent;
    private MailTemplateType mailTemplateType;
}
