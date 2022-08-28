package com.example.ieaapp;

public class EndUserChatModel {
    String email,message,name,color;

    public EndUserChatModel() {
    }

    public EndUserChatModel(String email, String message, String name) {
        this.email = email;
        this.message = message;
        this.name = name;
    }

    public EndUserChatModel(String email, String message, String name, String color) {
        this.email = email;
        this.message = message;
        this.name = name;
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

