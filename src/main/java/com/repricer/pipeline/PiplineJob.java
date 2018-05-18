package com.repricer.pipeline;

import com.repricer.Messaging.Message;
import com.repricer.Messaging.ServiceBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.lang.Thread.sleep;

public abstract class PiplineJob implements Runnable {

    public static final int BatchWindow = 1000; //in ms
    public static final int MaxBatchSize = 10;
    public static final int PollTimeout = 10;//in ms

    protected ServiceBus<Message> fromQueue; //receive from Batcher
    protected ServiceBus<Message> toQueue;  //sending to Writer

    protected Logger logger = LogManager.getLogger();

    public PiplineJob(ServiceBus<Message> from,ServiceBus<Message> to) {
        fromQueue  =  from;
        toQueue = to;
    }

    //////////////Running Interface ///////////////////////////////////

    protected abstract boolean shouldRun();
    protected abstract boolean ProcessMessage(Message m);
    protected abstract boolean IdleMessage();

    protected void preRun(){

    }

    protected void postRun(){

    }


    @Override
    public void run() {

        preRun();

        while (shouldRun()) {
            try {
                Message msg = fromQueue.poll(PollTimeout);
                if(msg != null)
                    ProcessMessage(msg);
                else
                    IdleMessage();
            }catch (Exception ex) {}
        }
        postRun();
    }

}
