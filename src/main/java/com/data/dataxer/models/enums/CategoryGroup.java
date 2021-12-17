package com.data.dataxer.models.enums;

public enum CategoryGroup {
    COMPANY("company"),
    SALARY("salary"),
    PROJECT("project"),
    TYPE_PROJECT("type_project"),
    CONTACT("contact");

    public final String categoryGroup;

    CategoryGroup(String categoryGroup) {
        this.categoryGroup = categoryGroup;
    }
}