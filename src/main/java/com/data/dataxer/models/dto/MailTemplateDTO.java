package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MailTemplateDTO {
    private Long id;
    private String emailSubject;
    private String emailContent;
    private LocalDateTime deletedAt;
}
