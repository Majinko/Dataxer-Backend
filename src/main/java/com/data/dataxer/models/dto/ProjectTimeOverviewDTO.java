package com.data.dataxer.models.dto;

import com.data.dataxer.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProjectTimeOverviewDTO {

    private List<TimeDTO> timeList;

    private String timeCurrentDate;
    private BigDecimal priceCurrentDate = BigDecimal.ZERO;

    private String timeForThisYear;

    public void setTimeList(List<TimeDTO> timeList) {
        this.timeList = timeList;

        Integer totalTime = 0;
        for (TimeDTO timeDTO : timeList) {
            totalTime += timeDTO.getTime();
            this.priceCurrentDate = this.priceCurrentDate.add(timeDTO.getPrice());
        }
        this.timeCurrentDate = StringUtils.convertMinutesTimeToHoursString(totalTime);
    }

}
