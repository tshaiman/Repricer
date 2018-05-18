package com.repricer.pipeline;

import com.repricer.Messaging.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.concurrent.*;

public class Pricer extends PiplineJob {

    private Random rand = new Random(System.currentTimeMillis());
    private static AtomicLong at ;
    static {
        at = new AtomicLong(0);
    }
    public Pricer(ServiceBus<Message> batcherQ, ServiceBus<Message> writerQ) {
        super(batcherQ,writerQ);
    }

    @Override
    protected boolean shouldRun() {
        return true;
    }

    @Override
    protected boolean ProcessMessage(Message m) {
        BulkMessage bulk = (BulkMessage) m;
        if(bulk == null)
            return false;

        List<RepricerMessage> result = bulk.getBulk().stream().map(pr->{
            RepricerMessage reprice = new RepricerMessage();
            double low = pr.getLower();
            double high = pr.getUpper();
            reprice.productId = pr.getProductId();
            reprice.priceLowerBound = low;
            reprice.priceUpperBound = high;
            reprice.prevPrice = pr.getCurrent();
            reprice.newPrice = ThreadLocalRandom.current().nextDouble(low,high);
            at.incrementAndGet();
            return reprice;
        }).collect(Collectors.toList());

        //Send the Result to the Output Writer
        result.forEach(r-> System.out.println("old : " + r.prevPrice + " , new : " + r.newPrice + " TID " + Thread.currentThread().getId()));
        System.out.println("Current counter is " + at.get());
        return true;
    }

    @Override
    protected boolean IdleMessage() {
        return false;
    }


}
