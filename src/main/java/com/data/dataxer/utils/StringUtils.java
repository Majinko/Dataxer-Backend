package com.data.dataxer.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {
    public static String removeWhiteLetters(String input) {
        return input.replaceAll("\\s+", "");
    }

    public static int countCharacters(String source, char toCheck) {
        return (int) source.chars().filter(ch -> ch == toCheck).count();
    }

    public static String generateString(String base, int length) {
        StringBuilder generated = new StringBuilder();
        while (generated.length() < length) {
            generated.append(base);
        }
        return generated.toString();
    }

    public static String generateRandomTextPassword() {
        String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
        String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
        String numbers = RandomStringUtils.randomNumeric(2);
        String totalChars = RandomStringUtils.randomAlphanumeric(4);
        String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
                .concat(numbers)
                .concat(totalChars);

        List<Character> pwdChars = combinedChars.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());

        Collections.shuffle(pwdChars);

        return pwdChars.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    public static String convertMinutesTimeToHoursString(Integer seconds) {
        return seconds / 3600 + ":" + (seconds % 3600) / 60 + " h";
    }

    public static String getAppUserFullName(String firstName, String lastName) {
        return firstName + " " + lastName;
    }
}
