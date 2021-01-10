package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MailDataDTO {

    private String subject;
    private String content;
    private Long companyId;
    private List<Long> participantIds;

}
