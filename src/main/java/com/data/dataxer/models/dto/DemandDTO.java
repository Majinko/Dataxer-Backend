package com.data.dataxer.models.dto;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.domain.Contact;
import com.data.dataxer.models.enums.DocumentState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DemandDTO {
    private Long id;
    private Category category;
    private Contact contact;
    private String title;
    private String description;
    private String source;
    private String state;
}
