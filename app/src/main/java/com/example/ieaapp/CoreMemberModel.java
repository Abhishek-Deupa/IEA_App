package com.example.ieaapp;

public class CoreMemberModel {

    String name, purl, company_name, company_address, phone_number, mail, designation;

    CoreMemberModel() {

    }

    public CoreMemberModel(String name, String purl, String company_name, String company_address, String phone_number, String mail, String designation) {
        this.name = name;
        this.purl = purl;
        this.company_name = company_name;
        this.company_address = company_address;
        this.phone_number = phone_number;
        this.mail = mail;
        this.designation = designation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPurl() {
        return purl;
    }

    public void setPurl(String purl) {
        this.purl = purl;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_address() {
        return company_address;
    }

    public void setCompany_address(String company_address) {
        this.company_address = company_address;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
