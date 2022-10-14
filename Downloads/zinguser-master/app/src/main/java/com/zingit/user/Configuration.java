package com.zingit.user;

public class Configuration {

    double tax;
    public Configuration(){}

    public Configuration(double tax) {
        this.tax = tax;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }


}
