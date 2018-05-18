package com.repricer.utils;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:global.properties")
public class ConfigProperties {

    @Value("${window}")
    private int window;
    @Value("${batch-size}")
    private int batchSize;
    @Value("${poll-time}")
    private int pollTime ;
    @Value("${max-concurrency}")
    private int maxConcurrency;
    @Value("${output-path}")
    private String outputPath;

    public int getWindow(){return window;}
    public int getBatchSize(){return batchSize;}
    public int getPollTime(){return pollTime;}
    public int getMaxConcurrency(){return maxConcurrency;}
    public String getOutputPath(){return outputPath;}




}
