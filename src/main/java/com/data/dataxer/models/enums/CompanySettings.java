package com.data.dataxer.models.enums;

public enum CompanySettings {

    FILE_UPLOAD_DIRECTORY("fileUploadDirectory");

    private String name;

    CompanySettings(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
