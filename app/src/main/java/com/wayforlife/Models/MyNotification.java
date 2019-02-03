package com.wayforlife.Models;

import java.util.Objects;

public class MyNotification {
    public String title;
    public String description;
    public String cityState;
    public String timeDate;

    public MyNotification() {
    }

    public MyNotification(String title, String description, String cityState, String timeDate) {
        this.title = title;
        this.description = description;
        this.cityState = cityState;
        this.timeDate = timeDate;
    }

    public String getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(String timeDate) {
        this.timeDate = timeDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCityState() {
        return cityState;
    }

    public void setCityState(String cityState) {
        this.cityState = cityState;
    }
}
