package com.repricer.pipeline;


import com.repricer.Messaging.BulkMessage;
import com.repricer.Messaging.Message;
import com.repricer.Messaging.ServiceBus;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Thread.sleep;

public class Batcher extends PiplineJob{

    //Settings
    public static final int BatchWindow = 1000; //in ms
    public static final int MaxBatchSize = 10;
    public static final int PollTimeout = 10;//in ms

    //Message Queues : from Dispatcher -> ToPricer
    private AtomicLong at = new AtomicLong(0);

    private List<Message> buffer  = new LinkedList<>();

    private long bulkTs = 0;

    public Batcher(ServiceBus<Message> dispatcherQ, ServiceBus<Message> pricerQ){
        super(dispatcherQ,pricerQ);
    }


    @Override
    protected boolean ProcessMessage(Message m) {
        long diff = m.getReceivedTime() - bulkTs;
        if (diff > BatchWindow)
            flushBulk();
        addToBulk(m);
        return true;
    }

    @Override
    protected boolean IdleMessage() {
        try {
            sleep(100);
            if (System.currentTimeMillis() - bulkTs > BatchWindow && buffer.size() > 0)
                flushBulk();
            return true;
        }catch (Exception e) {return false;}
    }


    private void addToBulk(Message m) {
        buffer.add(m);
        if(buffer.size() == MaxBatchSize)
            flushBulk();
    }

    private void flushBulk() {
        //System.out.println("Sending bulk to next component " + at.incrementAndGet());
        BulkMessage bulk = new BulkMessage(buffer);
        buffer.clear();
        bulkTs = System.currentTimeMillis();

        //send to pricer flow
        toQueue.put(bulk);
    }


}
