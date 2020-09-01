package com.data.dataxer.filters;

public enum SearchableDecimals {

    PRICE("price"),
    TOTAL_PRICE("totalPrice"),
    DISCOUNT("discount");

    private String value;

    SearchableDecimals(String value) {
        this.value = value;
    }

    public static boolean isSearchableDecimal(String columnId) {
        for (SearchableDecimals searchableDecimals : values()) {
            if (searchableDecimals.getValue().equals(columnId)) {
                return true;
            }
        }
        return false;
    }

    public String getValue() {
        return this.value;
    }
}
