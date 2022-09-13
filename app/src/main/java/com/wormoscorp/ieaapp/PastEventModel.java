package com.wormoscorp.ieaapp;

public class PastEventModel {
    String date, description, imgUrl, lowercase_title, location,weekday, time,title;

    PastEventModel() {

    }

    public PastEventModel(String date, String description, String imgUrl, String lowercase_title, String location, String weekday, String time,String title) {
        this.date = date;
        this.description = description;
        this.imgUrl = imgUrl;
        this.lowercase_title = lowercase_title;
        this.location = location;
        this.weekday = weekday;
        this.time = time;
        this.title=title;
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

    public String getLowercase_title() {
        return lowercase_title;
    }

    public void setLowercase_title(String lowercase_title) {
        this.lowercase_title = lowercase_title;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
