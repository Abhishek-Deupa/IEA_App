package com.example.ieaapp;

public class EventMemberItemModel {
    String email, imageUrl;

    public EventMemberItemModel() {

    }

    public EventMemberItemModel(String email, String imageUrl) {
        this.email = email;
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
