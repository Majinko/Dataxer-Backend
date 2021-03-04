package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class UserYearOverviewDTO {

    private String firstName;
    private String lastName;

    private HashMap<Integer, String> yearHours;

}
