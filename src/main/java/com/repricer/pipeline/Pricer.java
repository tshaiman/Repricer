package com.repricer.pipeline;

import com.repricer.Messaging.*;
import com.repricer.utils.ConfigProperties;
import com.repricer.utils.Monitor;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Pricer extends PiplineJob {


    public Pricer(ServiceBus<Message> batcherQ, ServiceBus<Message> writerQ, ConfigProperties config) {
        super(batcherQ,writerQ,config);
    }


    @Override
    protected boolean ProcessMessage(Message m) {
        BulkMessage<Message> bulk = (BulkMessage) m;
        if(bulk == null)
            return false;
        if(bulk.isEmpty())
            return false;

        List<RepricerMessage> outputBatch = bulk.getBulk().stream().map(pr->{
            RequestMessage pricer = (RequestMessage)pr;
            RepricerMessage reprice = new RepricerMessage();
            double low = pricer.getLower();
            double high = pricer.getUpper();
            reprice.productId = pricer.getProductId();
            reprice.priceLowerBound = low;
            reprice.priceUpperBound = high;
            reprice.prevPrice = pricer.getCurrent();
            reprice.newPrice = ThreadLocalRandom.current().nextDouble(low,high);
            //Monitor
            Monitor.pricerCounter.incrementAndGet();
            return reprice;
        }).collect(Collectors.toList());

        //Send the Result to the Output Writer
        BulkMessage<RepricerMessage> bulkMessage = new BulkMessage<>(outputBatch);
        toQueue.put(bulkMessage);

        return true;
    }



}
