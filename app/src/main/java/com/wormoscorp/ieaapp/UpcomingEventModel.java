package com.wormoscorp.ieaapp;

public class UpcomingEventModel {
    String title, date, time, date_and_time, joining_link, description, imgUrl, location, weekday;

    public UpcomingEventModel() {

    }

    public UpcomingEventModel(String title, String date, String time, String date_and_time, String joining_link, String description, String imgUrl, String location, String weekday) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.date_and_time = date_and_time;
        this.joining_link = joining_link;
        this.description = description;
        this.imgUrl = imgUrl;
        this.location = location;
        this.weekday = weekday;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getDate_and_time() {
        return date_and_time;
    }

    public void setDate_and_time(String date_and_time) {
        this.date_and_time = date_and_time;
    }

    public String getJoining_link() {
        return joining_link;
    }

    public void setJoining_link(String joining_link) {
        this.joining_link = joining_link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }
}