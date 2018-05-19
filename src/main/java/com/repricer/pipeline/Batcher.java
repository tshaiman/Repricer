package com.repricer.pipeline;


import com.repricer.Messaging.BulkMessage;
import com.repricer.Messaging.Message;
import com.repricer.Messaging.ServiceBus;
import com.repricer.utils.ConfigProperties;
import com.repricer.utils.Monitor;

import java.util.LinkedList;
import java.util.List;

import static java.lang.Thread.sleep;

public class Batcher extends PiplineJob{

    private List<Message> buffer  = new LinkedList<>();
    private long bulkTs = 0;
    private int window ;
    private int maxBatchSize ;

    public Batcher(ServiceBus<Message> dispatcherQ, ServiceBus<Message> pricerQ,ConfigProperties props){
        super(dispatcherQ,pricerQ,props);
        window = configProperties.getWindow();
        maxBatchSize = configProperties.getBatchSize();
    }

    @Override
    protected boolean ProcessMessage(Message m) {
        long diff = m.getReceivedTime() - bulkTs;
        if (diff > window)
            flushBulk();
        addToBulk(m);

        //Monitor
        Monitor.batcherCounter.incrementAndGet();

        return true;
    }

    @Override
    protected boolean IdleMessage() {
        try {
            sleep(50);
            if (System.currentTimeMillis() - bulkTs > window && buffer.size() > 0)
                flushBulk();
            return true;
        }catch (Exception e) {
            logger.error(e.getMessage());
            return false;}
    }


    private void addToBulk(Message m) {
        buffer.add(m);
        if(buffer.size() == maxBatchSize)
            flushBulk();
    }

    private void flushBulk() {
        BulkMessage<Message> bulk = new BulkMessage<>(buffer);
        buffer.clear();
        bulkTs = System.currentTimeMillis();

        toQueue.put(bulk);
    }


}
