package com.data.dataxer.utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Helpers {
    public static <T> Collector<T, ?, T> toSingleton() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        throw new IllegalStateException();
                    }
                    return list.get(0);
                }
        );
    }


    public static Long getDiffYears(LocalDate first, LocalDate last) {
        return ChronoUnit.YEARS.between(first, last);
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }
}
