package com.repricer;

import com.repricer.Messaging.Message;
import com.repricer.Messaging.PricerMessage;
import com.repricer.pipeline.Batcher;
import com.repricer.Messaging.ServiceBus;
import com.repricer.pipeline.FileWriter;
import com.repricer.pipeline.Pricer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;


@SpringBootApplication
public class Application implements ApplicationRunner {


    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {


        ServiceBus<Message> q1 = new ServiceBus<>();
        ServiceBus<Message> q2 = new ServiceBus<>();
        ServiceBus<Message> q3 = new ServiceBus<>();


        Batcher b = new Batcher(q1,q2);
        Pricer pricer = new Pricer(q2,q3);
        FileWriter fileWriter = new FileWriter(q3,null);

        ExecutorService executorService = Executors.newFixedThreadPool(16);
        executorService.execute(b);
        //3 workers on Pricer
        executorService.execute(pricer);
        executorService.execute(pricer);
        executorService.execute(pricer);

        //4 workers on FileWriter
        executorService.execute(fileWriter);
        executorService.execute(fileWriter);
        //executorService.execute(fileWriter);
        //executorService.execute(fileWriter);


        IntStream.range(0, 10)
                .forEach(ct -> executorService.execute(() -> {
                    for(int i = 0; i < 100; ++i){
                        PricerMessage p = new PricerMessage(UUID.randomUUID().toString(),1,2,10);
                        q1.put(p);
                    }
                }));

        Thread.sleep(1000);
        PricerMessage p = new PricerMessage(UUID.randomUUID().toString(),1,2,10);
        q1.put(p);
        Thread.sleep(2000);
        p = new PricerMessage(UUID.randomUUID().toString(),1,2,10);
        q1.put(p);

        Thread.sleep(60000);

    }
}