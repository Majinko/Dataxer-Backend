package com.data.dataxer.models.enums;

public enum CategoryType {
    NOT_INCLUDED("not_included"), // Nezahrnuté
    SALARY("salary"), // Mzdy prevádzka firmy
    SERVICE("service"), // Tovar / služba
    TIME("time"), // Časový záznam
    MAIN_TIME("main_time"), // Časové záznamy hlavných činností
    SECONDARY_TIME("secondary_time"), // Časové záznamy vedľajších činností,
    AFTER_END_TIME("after_end_time"); // Časové záznamy servisných činností po ukončení zákazky

    public final String categoryType;

    CategoryType(String categoryType) {
        this.categoryType = categoryType;
    }
    }
