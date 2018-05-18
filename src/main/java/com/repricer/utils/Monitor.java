package com.repricer.utils;

import java.util.concurrent.atomic.AtomicLong;

public class Monitor {

    public static AtomicLong writerCounter = new AtomicLong(0);
    public static AtomicLong pricerCounter = new AtomicLong(0);
    public static AtomicLong batcherCounter = new AtomicLong(0);


    //TODO : much more...........log rates , use StatsD , send to Graphana / Splunk / gather more metrics.......

}
