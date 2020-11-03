package com.data.dataxer.models.dto;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.domain.Company;
import com.data.dataxer.models.domain.Project;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TimeDTO {

    private Long id;
    private Integer time;
    private Integer timeFrom;
    private Integer timeTo;
    private BigDecimal price;
    private String description;
    private LocalDateTime dateWork;

    private Project project;
    private AppUser user;
    private Category category;

}
