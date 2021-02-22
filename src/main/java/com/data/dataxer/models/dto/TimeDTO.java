package com.data.dataxer.models.dto;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.domain.Project;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeDTO {

    private Long id;
    private Integer time;
    private Integer timeFrom;
    private Integer timeTo;
    private BigDecimal price;
    private String description;
    private LocalDate dateWork;
    private Float km;


    @NotNull
    private Project project;
    @NotNull
    private Category category;

    private AppUser user;
}
