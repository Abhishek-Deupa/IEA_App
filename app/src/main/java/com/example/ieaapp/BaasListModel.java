package com.example.ieaapp;

import java.util.ArrayList;

public class BaasListModel {

    String company_name, email, company_logo, industry_type;
    ArrayList<BaasListRecyclerModel> baasListRecyclerModels;

    BaasListModel() {

    }

    public BaasListModel(String company_name, String email, String company_logo, String industry_type, ArrayList<BaasListRecyclerModel> baasListRecyclerModels) {
        this.company_name = company_name;
        this.email = email;
        this.company_logo = company_logo;
        this.industry_type = industry_type;
        this.baasListRecyclerModels = baasListRecyclerModels;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany_logo() {
        return company_logo;
    }

    public void setCompany_logo(String company_logo) {
        this.company_logo = company_logo;
    }

    public String getIndustry_type() {
        return industry_type;
    }

    public void setIndustry_type(String industry_type) {
        this.industry_type = industry_type;
    }

    public ArrayList<BaasListRecyclerModel> getBaasListRecyclerModels() {
        return baasListRecyclerModels;
    }

    public void setBaasListRecyclerModels(ArrayList<BaasListRecyclerModel> baasListRecyclerModels) {
        this.baasListRecyclerModels = baasListRecyclerModels;
    }
}
