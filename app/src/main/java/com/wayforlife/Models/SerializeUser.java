package com.wayforlife.Models;

import java.io.Serializable;

public class SerializeUser implements Serializable {

    public transient User user; //Transient keyword will

    public SerializeUser(User user) {
        this.user = user;
    }

    public SerializeUser() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
