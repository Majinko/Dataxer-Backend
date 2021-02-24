package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthAndYearDTO {

    public MonthAndYearDTO(Integer year, Integer month) {
        this.year = year;
        this.month = month;
    }

    private Integer year;

    private Integer month;

}
