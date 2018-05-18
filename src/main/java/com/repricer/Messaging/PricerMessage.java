package com.repricer.Messaging;

public class PricerMessage extends Message {
    private String productId ;
    private double current;
    private double lower;
    private double upper;

    public PricerMessage(String pId,double current,double priceLowerBound,double priceUpperBound){
        productId = pId;
        this.current = current;
        this.lower = priceLowerBound;
        this.upper = priceUpperBound;
    }

    public String getProductId() {
        return productId;
    }

    public double getLower() {
        return lower;
    }

    public double getUpper() {
        return upper;
    }

    public double getCurrent() {
        return current;
    }

}
