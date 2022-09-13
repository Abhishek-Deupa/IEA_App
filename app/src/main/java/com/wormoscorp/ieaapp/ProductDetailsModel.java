package com.wormoscorp.ieaapp;

public class ProductDetailsModel {
    String ownerEmail,ownerPhoneNumber,productDescription,productImageUrl,productPrice,productTitle,productTitleLowerCase;

    public ProductDetailsModel(String ownerEmail, String ownerPhoneNumber, String productDescription, String productImageUrl, String productPrice, String productTitle, String productTitleLowerCase) {
        this.ownerEmail = ownerEmail;
        this.ownerPhoneNumber = ownerPhoneNumber;
        this.productDescription = productDescription;
        this.productImageUrl = productImageUrl;
        this.productPrice = productPrice;
        this.productTitle = productTitle;
        this.productTitleLowerCase = productTitleLowerCase;
    }

    public ProductDetailsModel() {
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerPhoneNumber() {
        return ownerPhoneNumber;
    }

    public void setOwnerPhoneNumber(String ownerPhoneNumber) {
        this.ownerPhoneNumber = ownerPhoneNumber;
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

    public String getProductTitleLowerCase() {
        return productTitleLowerCase;
    }

    public void setProductTitleLowerCase(String productTitleLowerCase) {
        this.productTitleLowerCase = productTitleLowerCase;
    }
}
