package com.repricer;

import java.util.concurrent.atomic.AtomicLong;

import com.repricer.Messaging.Message;
import com.repricer.Messaging.RequestMessage;
import com.repricer.Messaging.ServiceBus;
import com.repricer.utils.ConfigProperties;
import com.repricer.utils.ResourceThrottler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RepricerController {

    private final AtomicLong counter = new AtomicLong();
    private static Logger logger = LogManager.getLogger();
    public static final int DEFAULT_MAX_CONCURRENCY = 50;
    private static int MAX_CONCURRENCY;


    private ResourceThrottler throller;
    private ServiceBus<Message> dispatcherQueue;


    @Autowired
    public void setConfig(ConfigProperties config) {
        MAX_CONCURRENCY = Math.max(DEFAULT_MAX_CONCURRENCY, config.getMaxConcurrency());

        throller = new ResourceThrottler(MAX_CONCURRENCY);
        //we can now build pipeline
        dispatcherQueue = PipelineBuilder.setUpPipeline(config);
    }


    @RequestMapping("//reprice")
    public ResponseEntity<String> reprice(@RequestParam(value = "productId", defaultValue = "") String prodId,
                                          @RequestParam(value = "currentPrice", required = false,defaultValue = "0") String curPrice,
                                          @RequestParam(value = "priceLowerBound", required = false,defaultValue = "0") String lower,
                                          @RequestParam(value = "priceUpperBound", required = false,defaultValue = "0") String upper){

        //Check for available Slots
        if (!throller.tryServe())
            return new ResponseEntity<>("OK", HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);

        try {
            //Build new Pricer-Request message
            RequestMessage request = new RequestMessage(prodId, Double.parseDouble(curPrice),Double.parseDouble(lower),Double.parseDouble(upper));
            if(!request.validate())
                return new ResponseEntity<>("Bad Pararms", HttpStatus.BAD_REQUEST);

            //Finally !!Send to the Dispatcher Q
            dispatcherQueue.put(request);
            return new ResponseEntity<>("OK", HttpStatus.OK);

        } finally {
            throller.endServe();
        }


    }


}
