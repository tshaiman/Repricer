package com.repricer.Messaging;

public abstract class Message {
    protected long timestamp ;
    public Message(){
        timestamp = System.currentTimeMillis();
    }

    public long getReceivedTime(){
        return timestamp;
    }
}
