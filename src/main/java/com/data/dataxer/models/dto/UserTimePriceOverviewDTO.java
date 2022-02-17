package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

@Getter
@Setter
public class UserTimePriceOverviewDTO implements Comparable<UserTimePriceOverviewDTO> {

    private String name;
    private String photoUrl;
    private Integer hours;

    private BigDecimal hourNetto;
    private BigDecimal priceNetto;

    private BigDecimal hourBrutto;
    private BigDecimal priceBrutto;

    @Override
    public int compareTo(@NotNull UserTimePriceOverviewDTO user) {
        return user.getHours().compareTo(this.getHours());
    }
}

