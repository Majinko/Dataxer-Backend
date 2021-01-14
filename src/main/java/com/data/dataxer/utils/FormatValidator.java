package com.data.dataxer.utils;

import com.data.dataxer.exceptions.ValidationException;
import com.data.dataxer.models.enums.Periods;

public class FormatValidator {

    public static void validateFormat(String format, Periods period) {
        if (format.length() <= 0) {
            throw new ValidationException("Format is empty");
        }
        checkNumberInFormat(format);
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

    private static void checkNumberInFormat(String format) {
        int countOfNumber = StringUtils.countCharacters(format, 'N');
        String subNumber = StringUtils.generateString("N", countOfNumber);

        String restOfFormat = format.replace(subNumber, "");
        if (restOfFormat.contains("N")) {
            throw new ValidationException("Not correctly used symbol N");
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
        String subDay = StringUtils.generateString("D", countOfDaily);

        String restOfFormat = format.replace(subDay, "");
        if (restOfFormat.contains("D")) {
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
        String subMonth = StringUtils.generateString("M", countOfMonths);

        String restOfFormat = format.replace(subMonth, "");
        if (restOfFormat.contains("M")) {
            throw new ValidationException("Not correctly used symbol M");
        }
    }

    private static void checkQuarterInFormat(String format, boolean isQuarterPeriod) {
        int countOfQuarter = StringUtils.countCharacters(format, 'Q');
        if (isQuarterPeriod && countOfQuarter == 0) {
            throw new ValidationException("Missing symbol Q");
        }
        String subQuarter = StringUtils.generateString("Q", countOfQuarter);

        String restOfFormat = format.replace(subQuarter, "");
        if (restOfFormat.contains("Q")) {
            throw new ValidationException("Not correctly used symbol Q");
        }
    }

    private static void checkHalfYearInFormat(String format, boolean isHalfYearPeriod) {
        int countOfHalfYear = StringUtils.countCharacters(format, 'H');
        if (isHalfYearPeriod && countOfHalfYear == 0) {
            throw new ValidationException("Missing symbol H");
        }
        String subHalfYear = StringUtils.generateString("H", countOfHalfYear);

        String restOfFormat = format.replace(subHalfYear, "");
        if (restOfFormat.contains("H")) {
            throw new ValidationException("Not correctly used symbol H");
        }
    }

    private static void checkYearInFormat(String format, boolean isYearPeriod) {
        String restOfFormat = "";
        switch (StringUtils.countCharacters(format, 'Y')) {
            case 0:
                if (isYearPeriod) {
                    throw new ValidationException("Missing symbol Y");
                }
                break;
            case 2:
                restOfFormat = format.replace("YY", "");
                break;
            case 4:
                restOfFormat = format.replace("YYYY", "");
                break;
            default:
                throw new ValidationException("Not correct number Y symbols");
        }
        if (restOfFormat.contains("Y")) {
            throw new ValidationException("Not correctly used symbol Y");
        }
    }
}
