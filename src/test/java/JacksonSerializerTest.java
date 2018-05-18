import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.repricer.Messaging.RepricerMessage;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class JacksonSerializerTest {

    @Test
    public void whenSerializeAndDeserializeUsingJackson_thenCorrect()
            throws IOException {

        RepricerMessage repricerMessage = new RepricerMessage("a",2,1,10,5);
        ObjectMapper mapper = new ObjectMapper();

        String jsonStr = mapper.writeValueAsString(repricerMessage);
        RepricerMessage result = mapper.readValue(jsonStr, RepricerMessage.class);
        assertEquals(repricerMessage.productId,result.productId);
    }

    @Test
    public void whenWritingToFile_thenFileExistsAndDeserialize() throws  IOException {

        try {
            RepricerMessage r1 = new RepricerMessage("a", 2, 1, 10, 5);
            RepricerMessage r2 = new RepricerMessage("a", 2, 1, 10, 5);
            //RepricerMessage[] arr = new RepricerMessage[]{r1, r2};
            ArrayList<RepricerMessage> lst= new ArrayList<>();
            lst.add(r1);
            lst.add(r2);

            ObjectMapper mapper = new ObjectMapper();

            ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
            writer.writeValue(new File("data.json"), lst);

            File fReader = new File("data.json");
            assert (fReader.exists());

            RepricerMessage[] output = mapper.readValue(fReader, RepricerMessage[].class);
            assert (output != null);
            assertEquals(output.length, 2);
            assertEquals(output[0].productId, r1.productId);
            assertEquals(output[0].getReceivedTime(), r1.getReceivedTime());
        }
        finally {
            File fReader = new File("data.json");
            if(fReader.exists())
                fReader.delete();
        }

    }
}
