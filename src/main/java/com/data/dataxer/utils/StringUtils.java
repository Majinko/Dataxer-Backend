package com.data.dataxer.utils;

public class StringUtils {

    public static String removeWhiteLetters(String input) {
        return input.replaceAll("\\s+","");
    }

    public static int countCharacters(String source, char toCheck) {
        return (int) source.chars().filter(ch -> ch == toCheck).count();
    }

    public static String generateString(String base, int length) {
        String generated = "";
        while (generated.length() < length) {
            generated += base;
        }
        return generated;
    }

}
