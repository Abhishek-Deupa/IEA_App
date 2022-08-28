package com.example.ieaapp;

public class BaasListRecyclerModel {
    String ownerEmail, productImageUrl, productDescription, productPrice, productTitle;

    BaasListRecyclerModel() {

    }

    public BaasListRecyclerModel(String ownerEmail, String productImageUrl, String productDescription, String productPrice, String productTitle) {
        this.ownerEmail = ownerEmail;
        this.productImageUrl = productImageUrl;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.productTitle = productTitle;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }
}
