package com.repricer.pipeline;

import com.repricer.Messaging.Message;
import com.repricer.Messaging.ServiceBus;
import com.repricer.utils.ConfigProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import static java.lang.Thread.sleep;

public abstract class PiplineJob implements Runnable {


    protected ServiceBus<Message> fromQueue; //receive from Batcher
    protected ServiceBus<Message> toQueue;  //sending to Writer

    protected Logger logger = LogManager.getLogger();

    private int pollTime;

    //The Injected Properties
    ConfigProperties configProperties  = null;


    public PiplineJob(ServiceBus<Message> from,ServiceBus<Message> to,ConfigProperties config) {
        fromQueue  =  from;
        toQueue = to;
        this.configProperties = config;
        pollTime = configProperties.getPollTime();
    }



    //////////////Running Interface ///////////////////////////////////
    protected abstract boolean ProcessMessage(Message m);


    protected boolean IdleMessage(){
        return true;
    }

    protected void preRun(){

    }

    protected void postRun(){

    }
    protected boolean shouldRun(){
        return true;
    }


    @Override
    public void run() {

        preRun();

        while (shouldRun()) {
            try {
                Message msg = fromQueue.poll(pollTime);
                if(msg != null)
                    ProcessMessage(msg);
                else
                    IdleMessage();
            }catch (Exception ex) {}
        }
        postRun();
    }

}
