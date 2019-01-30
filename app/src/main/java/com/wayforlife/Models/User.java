package com.wayforlife.Models;

import java.util.HashMap;

public class User {
    public String firstName;
    public String lastName;
    public String email;
    public String phoneNumber;
    public String stateName;
    public String cityName;
    public String password;
    public String imageUrl;
    public HashMap<String,String> likesFeedHashMap;

    public static User currentUser;

    public User() {
    }

    public User(String firstName, String lastName, String email, String phoneNumber,
                String stateName, String cityName, String password, String imageUrl, HashMap<String, String> likesFeedHashMap) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.stateName = stateName;
        this.cityName = cityName;
        this.password = password;
        this.imageUrl = imageUrl;
        this.likesFeedHashMap = likesFeedHashMap;
    }

    public HashMap<String, String> getLikesFeedHashMap() {
        return likesFeedHashMap;
    }

    public void setLikesFeedHashMap(HashMap<String, String> likesFeedHashMap) {
        this.likesFeedHashMap = likesFeedHashMap;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public static User getCurrentUser() {
        if(currentUser!=null) {
            return currentUser;
        }
        return null;
    }

    public static void setCurrentUser(User currentUser) {
        User.currentUser = currentUser;
    }
}
