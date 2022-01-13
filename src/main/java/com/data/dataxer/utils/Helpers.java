package com.data.dataxer.utils;

import com.data.dataxer.securityContextUtils.SecurityUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
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

    public static String getDateFormat(LocalDate date) {
        SimpleDateFormat DateFor = new SimpleDateFormat("dd.MM.yyyy");

        return DateFor.format(date);
    }

    public static LinkedHashMap<Integer, BigDecimal> sortHashmapAndSubtractDiscount(HashMap<Integer, BigDecimal> originalMap, BigDecimal discount) {
        LinkedHashMap<Integer, BigDecimal> sorted = new LinkedHashMap<>();

        List<Integer> keys = new ArrayList<>(originalMap.keySet());
        Collections.sort(keys);
        Collections.reverse(keys);

        keys.forEach(key -> {
            if (discount != null && discount.compareTo(BigDecimal.ZERO) == 1) {
                sorted.put(key, originalMap.get(key).subtract(originalMap.get(key).multiply(discount.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP))
                        .setScale(2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP));
            } else {
                sorted.put(key, originalMap.get(key));
            }
        });

        return sorted;
    }

    public static LocalDate getLastDate(int minusMonth){
       return LocalDate.now().minusMonths(minusMonth);
    }
}
