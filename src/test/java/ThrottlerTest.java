import com.repricer.utils.ResourceThrottler;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;


public class ThrottlerTest {

    @Test
    public void givenThrottler_whenReachLimit_thenBlocked() throws InterruptedException {
        int slots = 40;
        ExecutorService executorService = Executors.newFixedThreadPool(slots);
        ResourceThrottler throttler = new ResourceThrottler(slots);
        IntStream.range(0, slots)
                .forEach(user -> executorService.execute(throttler::tryServe));
        executorService.shutdown();
        Thread.sleep(400);
        assertEquals(0, throttler.availableSlots());
        assertFalse(throttler.tryServe());
    }

    @Test
    public void givenThrottler_whenLogout_thenSlotsAvailable() throws InterruptedException {
        int slots = 40;
        ExecutorService executorService = Executors.newFixedThreadPool(slots);
        ResourceThrottler throttler = new ResourceThrottler(slots);
        IntStream.range(0, slots)
                .forEach(user -> executorService.execute(throttler::tryServe));
        executorService.shutdown();

        Thread.sleep(400);
        throttler.endServe();
        assertTrue(throttler.availableSlots() > 0);
        assertTrue(throttler.tryServe());
    }
}
