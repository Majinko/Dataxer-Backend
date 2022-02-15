package com.data.dataxer.models.dto;

public class NotificationDTO {
    private int count;

    public NotificationDTO(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void increment() {
        this.count++;
    }
}
