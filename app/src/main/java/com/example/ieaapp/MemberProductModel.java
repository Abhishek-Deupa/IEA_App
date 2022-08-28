package com.example.ieaapp;

public class MemberProductModel {

    String productDescription,productImageUrl,productPrice,productTitle,ownerEmail, productTitleLowerCase;

    public MemberProductModel() {
    }

    public MemberProductModel(String productDescription, String productImageUrl, String productPrice, String productTitle, String ownerEmail, String productTitleLowerCase) {
        this.productDescription = productDescription;
        this.productImageUrl = productImageUrl;
        this.productPrice = productPrice;
        this.productTitle = productTitle;
        this.ownerEmail = ownerEmail;
        this.productTitleLowerCase = productTitleLowerCase;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
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

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getProductTitleLowerCase() {
        return productTitleLowerCase;
    }

    public void setProductTitleLowerCase(String productTitleLowerCase) {
        this.productTitleLowerCase = productTitleLowerCase;
    }
}