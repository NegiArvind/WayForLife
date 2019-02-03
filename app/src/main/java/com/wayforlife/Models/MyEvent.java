package com.wayforlife.Models;

public class MyEvent {
    public long eventDate;
    public String eventName;
    public String eventDescription;

    public MyEvent() {
    }

    public MyEvent(long eventDate, String eventName, String eventDescription) {
        this.eventDate = eventDate;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
    }

    public long getEventDate() {
        return eventDate;
    }

    public void setEventDate(long eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }
}
