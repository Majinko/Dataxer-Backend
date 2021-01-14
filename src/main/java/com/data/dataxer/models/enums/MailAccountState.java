package com.data.dataxer.models.enums;

public enum MailAccountState {

    DEACTIVATED("deactivated"),
    ACTIVATED("activated"),
    PENDING("pending");

    private String state;

    MailAccountState(String state) {
        this.state = state;
    }

    public static MailAccountState getByName(String state) {
        return MailAccountState.valueOf(state);
    }

    public String getState() {
        return this.state;
    }

}
