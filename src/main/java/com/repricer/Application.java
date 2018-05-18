package com.repricer;

import com.repricer.Messaging.Message;
import com.repricer.Messaging.PricerMessage;
import com.repricer.pipeline.Batcher;
import com.repricer.Messaging.ServiceBus;
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
        List<Message> lst = new LinkedList<>();
        lst.add(new PricerMessage("a",1,2,10));
        lst.add(new PricerMessage("b",1,2,10));
        lst.add(new PricerMessage("c",1,2,10));

        PricerMessage[] arr = new PricerMessage[1];
        lst.toArray(arr);



        //4 Threads
        ServiceBus<Message> q1 = new ServiceBus<>();
        ServiceBus<Message> q2 = new ServiceBus<>();
        ServiceBus<Message> q3 = new ServiceBus<>();

        Batcher b = new Batcher(q1,q2);
        Pricer pricer = new Pricer(q2,q3);

        ExecutorService executorService = Executors.newFixedThreadPool(8);
        executorService.execute(b);
        executorService.execute(pricer);
        executorService.execute(pricer);
        executorService.execute(pricer);

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