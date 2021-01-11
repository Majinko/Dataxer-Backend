package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailTemplatesDTO {

    private Long id;
    private String emailSubject;
    private String emailContent;

}
