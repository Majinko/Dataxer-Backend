package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

@Getter
@Setter
public class AppUserOverviewDTO implements Comparable<AppUserOverviewDTO> {
    Long id;
    String uid;
    String fullName;
    LocalDate startWork;
    Long years;
    Long projectCount;
    Integer sumTime;
    SalaryDTO salaryDTO;

    @Override
    public int compareTo(@NotNull AppUserOverviewDTO appUserOverviewDTO) {
        if (appUserOverviewDTO.getSumTime() == null){
            return -1;
        }

        if (this.getSumTime() == null) {
            return 1;
        }

        return appUserOverviewDTO.getSumTime().compareTo(this.getSumTime());
    }
}
