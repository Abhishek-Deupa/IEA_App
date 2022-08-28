package com.example.ieaapp;

public class MemberNotificationModel {
    String notificationTitle, notificationContent, notificationDate;

    public MemberNotificationModel() {

    }

    public MemberNotificationModel(String notificationTitle, String notificationContent, String notificationDate) {
        this.notificationTitle = notificationTitle;
        this.notificationContent = notificationContent;
        this.notificationDate = notificationDate;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }

    public String getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(String notificationDate) {
        this.notificationDate = notificationDate;
    }
}
