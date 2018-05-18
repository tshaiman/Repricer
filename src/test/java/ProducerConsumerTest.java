import com.repricer.Messaging.ServiceBus;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class ProducerConsumerTest {

    @Test
    public void SingleWriter_MultipleReaderQ() {
        AtomicInteger at = new AtomicInteger();
        ServiceBus<Integer> pr = new ServiceBus<Integer>();

        ThreadPoolExecutor executorService =  (ThreadPoolExecutor)Executors.newFixedThreadPool(4);
        IntStream.range(0, 10)
                .forEach(ct -> executorService.execute(() -> pr.put(ct)));

        IntStream.range(0, 20)
                .forEach(ct -> executorService.execute(() -> {
                    Integer val =pr.poll(100);
                    if(val != null)
                        at.incrementAndGet();
                }));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(at.get(), 10);
    }

    @Test
    public void SingleWriter_MultipleReader_LessThreads() {
        AtomicInteger at = new AtomicInteger();
        ServiceBus<Integer> pr = new ServiceBus<Integer>();

        ThreadPoolExecutor executorService =  (ThreadPoolExecutor)Executors.newFixedThreadPool(4);
        IntStream.range(0, 10)
                .forEach(ct -> executorService.execute(() -> pr.put(ct)));

        IntStream.range(0, 2)
                .forEach(ct -> executorService.execute(() -> {
                    for(int j = 0 ; j < 100; ++j) {
                        Integer val = pr.poll(1);
                        if (val != null)
                            at.incrementAndGet();
                    }
                }));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(at.get(), 10);
    }
}
