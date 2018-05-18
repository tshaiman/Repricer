package com.repricer.Messaging;

public class RepricerMessage extends Message{

    public RepricerMessage(){

    }

    public RepricerMessage(String pId,double prev,double lower,double upper, double updatedPrice) {
        productId = pId;
        prevPrice = prev;
        priceLowerBound = lower;
        priceUpperBound = upper;
        newPrice = updatedPrice;
    }

    public String productId ;
    public double prevPrice;
    public double priceLowerBound;
    public double priceUpperBound;
    public double newPrice;

}
