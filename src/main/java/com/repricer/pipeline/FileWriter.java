package com.repricer.pipeline;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.repricer.Messaging.BulkMessage;
import com.repricer.Messaging.Message;
import com.repricer.Messaging.ServiceBus;
import com.repricer.utils.ConfigProperties;
import com.repricer.utils.Monitor;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FileWriter extends PiplineJob {


    DefaultPrettyPrinter pretty = new DefaultPrettyPrinter();
    ObjectMapper mapper = new ObjectMapper();
    ObjectWriter writer = mapper.writer(pretty);

    private String outputPath;

    public FileWriter(ServiceBus<Message> from, ServiceBus<Message> to, ConfigProperties config) {
        super(from, to, config);
        outputPath = config.getOutputPath();
        File dirs = new File(outputPath);
        if (!dirs.exists()) {
            if (dirs.mkdirs()) {
                logger.warn("Directory " + outputPath + " was created successfully!");

            } else {
                logger.error("Directory " + outputPath + " could not be created!");
            }
        }

    }

    @Override
    protected boolean ProcessMessage(Message m) {
        BulkMessage bulk = (BulkMessage) m;
        if (bulk == null || bulk.isEmpty()) {
            return false;
        }

        try {
            String fileName = String.format("%spricer_%s.json", outputPath, System.nanoTime());
            writer.writeValue(new File(fileName), bulk.getBulk().toArray());
            log(fileName, bulk.Size());


        } catch (IOException ex) {
            logger.error("Could not write file f. Reason " + ex.getMessage());
        }

        return true;
    }

    private void log(String fileName, int size) {
        logger.info("File Writer created file " + fileName + " with" + size + " items");
        //write to monitor
        Monitor.writerCounter.addAndGet(size);
    }


}
