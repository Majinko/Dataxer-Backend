package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MailDataDTO {
    private String subject;
    private String content;
    private List<String> emails;
    private List<String> fileNames;
}
