package com.data.dataxer.models.dto;

import com.data.dataxer.models.domain.Contact;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DemandDTO {
    private Long id;
    private String title;
    private String subject;
    private String description;
    private String source;
    private String state;
    private String note;
    private String number;

    private boolean internal;

    private LocalDate createdDate;
    private LocalDate deliveredDate;
    private LocalDate dueDate;

    private Map<String, Object> documentData;

    private ProjectDTO project;
    private CompanyDTO company;

    private List<Contact> contacts;
    private List<DemandPackDTO> packs;
}
