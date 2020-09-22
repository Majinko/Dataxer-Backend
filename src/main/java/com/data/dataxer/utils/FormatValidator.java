package com.data.dataxer.utils;

import com.data.dataxer.Exceptions.ValidationException;
import com.data.dataxer.models.enums.Periods;

public class FormatValidator {

    public static void validateFormat(String format, Periods period) {
        if (format.length() > 0) {
            throw new ValidationException("Format is empty");
        }
        switch (period) {
            case YEAR:
                checkYearInFormat(format, true);
                checkHalfYearInFormat(format, false);
                checkQuarterInFormat(format, false);
                checkMonthlyInFormat(format, false);
                checkDailyInFormat(format, false);
                break;
            case HALF_YEAR:
                checkYearInFormat(format,false);
                checkHalfYearInFormat(format, true);
                checkQuarterInFormat(format, false);
                checkMonthlyInFormat(format, false);
                checkDailyInFormat(format, false);
                break;
            case QUARTER:
                checkYearInFormat(format, false);
                checkHalfYearInFormat(format, false);
                checkQuarterInFormat(format, true);
                checkMonthlyInFormat(format, false);
                checkDailyInFormat(format, false);
                break;
            case MONTHLY:
                checkYearInFormat(format, false);
                checkHalfYearInFormat(format, false);
                checkQuarterInFormat(format, false);
                checkMonthlyInFormat(format, true);
                checkDailyInFormat(format, false);
                break;
            case DAILY:
                checkYearInFormat(format, false);
                checkHalfYearInFormat(format, false);
                checkQuarterInFormat(format, false);
                checkMonthlyInFormat(format, false);
                checkDailyInFormat(format, true);
                break;
        }
    }

    private static void checkDailyInFormat(String format, boolean isDailyPeriod) {
        int countOfDaily = StringUtils.countCharacters(format, 'D');
        if (isDailyPeriod && countOfDaily == 0) {
            throw new ValidationException("Missing symbol D");
        }
        if (countOfDaily > 2) {
            throw new ValidationException("Format contain too much D symbols");
        }
        String subDay = "";
        for (int i = 0; i < countOfDaily; i++) {
            subDay += "D";
        }
        format.replace(subDay, "");
        if (format.contains("D")) {
            throw new ValidationException("Not correctly used symbol D");
        }
    }

    private static void checkMonthlyInFormat(String format, boolean isMonthlyPeriod) {
        int countOfMonths = StringUtils.countCharacters(format, 'M');
        if (isMonthlyPeriod && countOfMonths == 0) {
            throw new ValidationException("Missing symbol M");
        }
        if (countOfMonths > 2) {
            throw new ValidationException("Format contain too much M symbols");
        }
        String subMonth = "";
        for (int i = 0; i < countOfMonths; i++) {
            subMonth += "M";
        }
        format.replace(subMonth, "");
        if (format.contains("M")) {
            throw new ValidationException("Not correctly used symbol M");
        }
    }

    private static void checkQuarterInFormat(String format, boolean isQuarterPeriod) {
        int countOfQuarter = StringUtils.countCharacters(format, 'Q');
        if (isQuarterPeriod && countOfQuarter == 0) {
            throw new ValidationException("Missing symbol Q");
        }
        String subQuarter = "";
        for (int i = 0; i < countOfQuarter; i++) {
            subQuarter += "Q";
        }
        format.replace(subQuarter, "");
        if (format.contains("Q")) {
            throw new ValidationException("Not correctly used symbol Q");
        }
    }

    private static void checkHalfYearInFormat(String format, boolean isHalfYearPeriod) {
        int countOfHalfYear = StringUtils.countCharacters(format, 'H');
        if (isHalfYearPeriod && countOfHalfYear == 0) {
            throw new ValidationException("Missing symbol H");
        }
        String subHalfYear = "";
        for (int i = 0; i < countOfHalfYear; i++) {
            subHalfYear += "H";
        }
        format.replace(subHalfYear, "");
        if (format.contains("H")) {
            throw new ValidationException("Not correctly used symbol H");
        }
    }

    private static void checkYearInFormat(String format, boolean isYearPeriod) {
        switch (StringUtils.countCharacters(format, 'Y')) {
            case 0:
                if (isYearPeriod) {
                    throw new ValidationException("Missing symbol Y");
                }
                break;
            case 2:
                format.replace("YY", "");
                break;
            case 4:
                format.replace("YYYY", "");
                break;
            default:
                throw new ValidationException("Not correct number Y symbols");
        }
        if (format.contains("Y")) {
            throw new ValidationException("Not correctly used symbol Y");
        }
    }
}
