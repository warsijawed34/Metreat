package com.metreat.model;

/**
 * Created by vinove on 25/8/16.
 */
public class NotificationModel {

    String userEventID;
    String receiverID;
    String eventType;
    String eventDate;
    String name;
    String age;


    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getUserEventID() {
        return userEventID;
    }

    public void setUserEventID(String userEventID) {
        this.userEventID = userEventID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
