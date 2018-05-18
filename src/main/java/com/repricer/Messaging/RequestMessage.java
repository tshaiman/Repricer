package com.repricer.Messaging;

public class RequestMessage extends Message {
    private String productId ;
    private double current;
    private double lower;
    private double upper;

    public RequestMessage(String pId, double current, double priceLowerBound, double priceUpperBound){
        productId = pId;
        this.current = current;
        this.lower = priceLowerBound;
        this.upper = priceUpperBound;
    }

    public boolean validate(){
        return productId.length() > 0
                && current >0
                && lower > 0
                && upper > 0
                && upper > lower;
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
