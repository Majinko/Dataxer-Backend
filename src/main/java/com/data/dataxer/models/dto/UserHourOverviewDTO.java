package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class UserHourOverviewDTO {

    HashMap<Integer, Integer> userHours;

}
