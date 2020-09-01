package com.data.dataxer.filters;

public enum SearchableDates {

    CREATED("createdDate"),
    DELIVERY("deliveredDate"),
    PAYMENT("paymentDate"),
    DUE("dueDate"),
    DELETED("deletedAt");

    private String value;

    SearchableDates(String value) {
        this.value = value;
    }

    public static boolean isSearchableDate(String columnId) {
        for (SearchableDates searchableDates : values()) {
            if (searchableDates.getValue().equals(columnId)) {
                return true;
            }
        }
        return false;
    }

    public String getValue() {
        return this.value;
    }
}
