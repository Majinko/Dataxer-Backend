package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MailDataDTO {
    private String subject;
    private String content;
<<<<<<< HEAD
    private List<String> emails;
=======
    private Long templateId;
    private Long companyId;
    private List<Long> participantIds;
    private List<String> fileNames;

>>>>>>> fd607e9a34fe0b835983eb89f14a40abe0874fbf
}
