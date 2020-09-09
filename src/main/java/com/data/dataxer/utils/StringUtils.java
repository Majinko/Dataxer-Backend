package com.data.dataxer.utils;

public class StringUtils {

    public static String removeWhiteLetters(String input) {
        return input.replaceAll("\\s+","");
    }

}
