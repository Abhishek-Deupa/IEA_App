package com.example.ieaapp;

public class MyGrievanceModel {
    String complain, department, status;

    public MyGrievanceModel(){

    }

    public MyGrievanceModel(String complain, String department, String status) {
        this.complain = complain;
        this.department = department;
        this.status = status;
    }

    public String getComplain() {
        return complain;
    }

    public void setComplain(String complain) {
        this.complain = complain;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
