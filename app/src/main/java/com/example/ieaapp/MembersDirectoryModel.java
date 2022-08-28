package com.example.ieaapp;

public class MembersDirectoryModel {

    String address, company_name, date_of_birth, date_of_membership, email, member_id, name, phone_number, purl;

    MembersDirectoryModel() {

    }

    public MembersDirectoryModel(String address, String company_name, String date_of_birth, String date_of_membership, String email, String member_id, String name, String phone_number, String purl, String industry_type) {
        this.address = address;
        this.company_name = company_name;
        this.date_of_birth = date_of_birth;
        this.date_of_membership = date_of_membership;
        this.email = email;
        this.member_id = member_id;
        this.name = name;
        this.phone_number = phone_number;
        this.purl = purl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getDate_of_membership() {
        return date_of_membership;
    }

    public void setDate_of_membership(String date_of_membership) {
        this.date_of_membership = date_of_membership;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPurl() {
        return purl;
    }

    public void setPurl(String purl) {
        this.purl = purl;
    }

}
