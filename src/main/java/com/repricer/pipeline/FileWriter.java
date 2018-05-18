package com.repricer.pipeline;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.repricer.Messaging.BulkMessage;
import com.repricer.Messaging.Message;
import com.repricer.Messaging.ServiceBus;

import java.io.File;
import java.io.IOException;

public class FileWriter extends PiplineJob {

    ObjectMapper mapper = new ObjectMapper();
    DefaultPrettyPrinter pretty = new DefaultPrettyPrinter();

    public FileWriter(ServiceBus<Message> from, ServiceBus<Message> to) {
        super(from, to);
    }

    @Override
    protected boolean ProcessMessage(Message m) {
        BulkMessage bulk = (BulkMessage)m;
        if(bulk == null)
            return false;

        try{
            ObjectWriter writer = mapper.writer(pretty);

            String fileName = "pricer_" + System.currentTimeMillis() + ".json";
            writer.writeValue(new File(fileName), bulk.getBulk().toArray());
            System.out.println("Created file for batch " + fileName);
        }catch (IOException ex){
            logger.error("Could not write file f. Reason " + ex.getMessage());
        }

        return true;
    }


}
