package com.wayforlife.Models;

public class Problem {

    public String imageUrl;
    public String date;
    public String time;
    public String description;
    public LocationAddress locationAddress;
    public String userId; // store the id of the user who has reported the problem

    public Problem(String imageUrl, String date, String time, String description, LocationAddress locationAddress, String userId) {
        this.imageUrl = imageUrl;
        this.date = date;
        this.time = time;
        this.description = description;
        this.locationAddress = locationAddress;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Problem() {
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public LocationAddress getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(LocationAddress locationAddress) {
        this.locationAddress = locationAddress;
    }
}
