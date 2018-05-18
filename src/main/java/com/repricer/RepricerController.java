package com.repricer;

import java.util.concurrent.atomic.AtomicLong;

import com.repricer.utils.ResourceThrottler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RepricerController {

    private final AtomicLong counter = new AtomicLong();
    private static Logger logger = LogManager.getLogger();

    private ResourceThrottler throller = new ResourceThrottler(50);

    @RequestMapping("/greeting")
    public ResponseEntity<String> greeting(@RequestParam(value = "name", defaultValue = "World") String name) {

        if (!throller.tryServe())
            return new ResponseEntity<>("OK", HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);

        try {
            System.out.println("I am being served. available " + throller.availableSlots());
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } finally {
            throller.endServe();
        }


    }


}
