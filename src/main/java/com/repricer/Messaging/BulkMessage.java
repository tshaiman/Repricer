package com.repricer.Messaging;

import java.util.ArrayList;
import java.util.List;

public class BulkMessage extends Message{

    private List<PricerMessage> bulk ;


    public BulkMessage(List<Message> lst){
        bulk = new ArrayList<>();
        for(int i= 0 ; i < lst.size() ; ++i)
            bulk.add((PricerMessage)lst.get(i));
    }

    public List<PricerMessage> getBulk() {
        return bulk;
    }
}
