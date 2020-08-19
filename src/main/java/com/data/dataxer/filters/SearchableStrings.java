package com.data.dataxer.filters;

public enum SearchableStrings {

    TITLE("title"),
    NUMBER("number"),
    NOTE("note");

    private String value;

    SearchableStrings(String value) {
        this.value = value;
    }

    public static boolean isSearchableString(String columnId) {
        for (SearchableStrings searchableStrings : values()) {
            if (searchableStrings.getValue().equals(columnId)) {
                return true;
            }
        }
        return false;
    }

    public String getValue() {
        return this.value;
    }

}
