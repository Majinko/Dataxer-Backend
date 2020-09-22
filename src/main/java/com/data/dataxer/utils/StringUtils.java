package com.data.dataxer.utils;

public class StringUtils {

    public static String removeWhiteLetters(String input) {
        return input.replaceAll("\\s+","");
    }

    public static int countCharacters(String source, char toCheck) {
        return (int) source.chars().filter(ch -> ch == toCheck).count();
    }

}
