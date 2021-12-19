package com.data.dataxer.utils;

import com.data.dataxer.securityContextUtils.SecurityUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
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
        if (first != null && last != null) {
            return ChronoUnit.YEARS.between(first, last);
        }

        return null;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

    public static void checkCompanyIdFromRql(String parseThis) {
        String[] strings = parseThis.split(".company.id==");
        Long companyId = Long.parseLong(strings[strings.length - 1].replaceAll("[^0-9?!\\.]", ""));

        if (!SecurityUtils.companyIds().contains(companyId)) {
            throw new RuntimeException("ha ha ha");
        }
    }

    public static String getDateFormat(LocalDate date) {
        SimpleDateFormat DateFor = new SimpleDateFormat("dd.MM.yyyy");

        return DateFor.format(date);
    }
}
