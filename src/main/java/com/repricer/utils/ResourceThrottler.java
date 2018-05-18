package com.repricer.utils;

import java.util.concurrent.Semaphore;

public class ResourceThrottler {
    private Semaphore semaphore;

    public ResourceThrottler(int slotLimit) {
        semaphore = new Semaphore(slotLimit);
    }

    public boolean tryServe() {
        return semaphore.tryAcquire();
    }

    public void endServe() {
        semaphore.release();
    }

    public int availableSlots() {
        return semaphore.availablePermits();
    }
}
