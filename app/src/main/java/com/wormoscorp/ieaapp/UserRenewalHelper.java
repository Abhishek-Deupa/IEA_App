package com.wormoscorp.ieaapp;

public class UserRenewalHelper {
    String purl, email, memberfee, amountleft, name, companyname, paymentReceiverName;

    public UserRenewalHelper() {
    }

    public UserRenewalHelper(String purl, String email, String memberfee, String amountleft, String name, String companyname, String paymentReceiverName) {
        this.purl = purl;
        this.email = email;
        this.memberfee = memberfee;
        this.amountleft = amountleft;
        this.name = name;
        this.companyname = companyname;
        this.paymentReceiverName = paymentReceiverName;
    }

    public String getPurl() {
        return purl;
    }

    public void setPurl(String purl) {
        this.purl = purl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMemberfee() {
        return memberfee;
    }

    public void setMemberfee(String memberfee) {
        this.memberfee = memberfee;
    }

    public String getAmountleft() {
        return amountleft;
    }

    public void setAmountleft(String amountleft) {
        this.amountleft = amountleft;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getPaymentReceiverName() {
        return paymentReceiverName;
    }

    public void setPaymentReceiverName(String paymentReceiverName) {
        this.paymentReceiverName = paymentReceiverName;
    }
}
