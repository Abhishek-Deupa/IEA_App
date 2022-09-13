package com.wormoscorp.ieaapp;

public class UserRegistrationHelperClass {
    String fullname, email, phoneNo, companyName, department, turnover,imageUrl,amountLeft,memberfee,paymentReceiverName, gstno;

    public  UserRegistrationHelperClass(){

    }

    public UserRegistrationHelperClass(String fullname, String email, String phoneNo, String companyName, String department, String turnover, String imageUrl, String amountLeft, String memberfee, String paymentReceiverName, String gstno) {
        this.fullname = fullname;
        this.email = email;
        this.phoneNo = phoneNo;
        this.companyName = companyName;
        this.department = department;
        this.turnover = turnover;
        this.imageUrl = imageUrl;
        this.amountLeft = amountLeft;
        this.memberfee = memberfee;
        this.paymentReceiverName = paymentReceiverName;
        this.gstno = gstno;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTurnover() {
        return turnover;
    }

    public void setTurnover(String turnover) {
        this.turnover = turnover;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAmountLeft() {
        return amountLeft;
    }

    public void setAmountLeft(String amountLeft) {
        this.amountLeft = amountLeft;
    }

    public String getMemberfee() {
        return memberfee;
    }

    public void setMemberfee(String memberfee) {
        this.memberfee = memberfee;
    }

    public String getPaymentReceiverName() {
        return paymentReceiverName;
    }

    public void setPaymentReceiverName(String paymentReceiverName) {
        this.paymentReceiverName = paymentReceiverName;
    }

    public String getGstno() {
        return gstno;
    }

    public void setGstno(String gstno) {
        this.gstno = gstno;
    }
}