package com.repricer.Messaging;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public abstract class Message {
    protected long timestamp ;
    public Message(){
        timestamp = System.currentTimeMillis();
    }

    @JsonGetter("timestamp")
    public long getReceivedTime(){
        return timestamp;
    }

    ///The Following is for Json Serde only !
    @JsonSetter("timestamp")
    public void setReceivedTime(long ts) {
        timestamp = ts;
    }
}
