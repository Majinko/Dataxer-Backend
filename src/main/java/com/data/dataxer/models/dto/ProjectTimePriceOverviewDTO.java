package com.data.dataxer.models.dto;

import com.data.dataxer.utils.StringUtils;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class ProjectTimePriceOverviewDTO {

    private final String name;
    private String hours;

    private final Integer seconds;
    private final BigDecimal costToHour;

    private BigDecimal hourNetto;
    private final BigDecimal priceNetto;

    private BigDecimal hourBrutto;
    private BigDecimal priceBrutto;

    public ProjectTimePriceOverviewDTO(String name, Integer seconds, BigDecimal costToHour, BigDecimal priceNetto) {
        this.name = name;
        this.seconds = seconds;
        setHours();

        this. costToHour = costToHour;

        this.priceNetto = priceNetto;
        setHourNetto(seconds, priceNetto);

        setHourBrutto();
        setPriceBrutto();
    }

    private void setHourNetto(Integer timeSum, BigDecimal priceSum) {
        this.hourNetto = this.countHourNetto(timeSum, priceSum);
    }

    private void setHourBrutto() {
        this.hourBrutto = BigDecimal.ZERO.add(hourNetto).add(costToHour);
    }

    private void setPriceBrutto() {
        this.priceBrutto = this.hourNetto.equals(this.hourBrutto) ? this.priceNetto :
                new BigDecimal(this.seconds / 3600).setScale(2, RoundingMode.HALF_UP).multiply(this.hourBrutto).setScale(2, RoundingMode.HALF_UP);
    }

    private void setHours() {
        this.hours = StringUtils.convertMinutesTimeToHoursString(this.seconds);
    }

    private BigDecimal countHourNetto(Integer timeSum, BigDecimal priceSum) {
        BigDecimal minutePrice = priceSum.divide(new BigDecimal(timeSum/60), 2 , RoundingMode.HALF_UP);

        return minutePrice.multiply(new BigDecimal(60)).setScale(2, RoundingMode.HALF_UP);
    }

}
