package com.metreat.model;

/**
 * Created by vinove on 22/8/16.
 */
public class syncContactsModel {
    String userId;
    String UserName;
    String email;
    String thumbUrl;
    String status;
    String number;
    String contacName;


    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public String getUserId() {
        return userId;

    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContacName() {
        return contacName;
    }

    public void setContacName(String contacName) {
        this.contacName = contacName;
    }
}
