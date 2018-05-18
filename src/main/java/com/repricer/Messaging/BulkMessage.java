package com.repricer.Messaging;

import java.util.ArrayList;
import java.util.List;

public class BulkMessage<T extends  Message> extends Message{

    private ArrayList<T> bulk ;

    public BulkMessage(List<T> lst){
        bulk = new ArrayList<>(lst);
    }

    public boolean isEmpty() {
        return bulk.isEmpty();
    }

    public int Size() {return bulk.size();}

    public List<T> getBulk() {
        return bulk;
    }


}
