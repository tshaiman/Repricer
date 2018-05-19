package com.repricer;

import com.repricer.Messaging.Message;
import com.repricer.Messaging.ServiceBus;
import com.repricer.pipeline.Batcher;
import com.repricer.pipeline.FileWriter;
import com.repricer.pipeline.Pricer;
import com.repricer.utils.ConfigProperties;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public  class PipelineBuilder {

    /**
     * SetUp a Pipelince for the repricer flow and return the Dispatcher Service Bus
     * @param config
     * @return
     * @throws Exception
     */

    public static ServiceBus<Message> setUpPipeline(ConfigProperties config)  {

        ServiceBus<Message> dispatcherQ = new ServiceBus<>();
        ServiceBus<Message> pricerQ = new ServiceBus<>();
        ServiceBus<Message> writerQ = new ServiceBus<>();


        Batcher batcher = new Batcher(dispatcherQ,pricerQ,config);
        Pricer pricer = new Pricer(pricerQ,writerQ,config);
        FileWriter fileWriter = new FileWriter(writerQ,null,config);

        ExecutorService service = Executors.newFixedThreadPool(16);


        //exactly One Batcher
        service.execute(batcher);

        //2 workers on Pricer
        service.execute(pricer);
        service.execute(pricer);


        //4 workers on FileWriter
        service.execute(fileWriter);
        service.execute(fileWriter);
        service.execute(fileWriter);
        service.execute(fileWriter);

        return dispatcherQ;




    }
}
