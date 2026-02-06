package com.AlbyBiShe.Crypto_Market_Data_MCP.binance;

public class WeightRateLimiter {

    private final int maxWeightPerMinute;
    private long windowStartMillis;
    private int usedWeight;

    public WeightRateLimiter(int maxWeightPerMinute) {
        if (maxWeightPerMinute <= 0) {
            throw new IllegalArgumentException("maxWeightPerMinute must be positive");
        }
        this.maxWeightPerMinute = maxWeightPerMinute;
        this.windowStartMillis = System.currentTimeMillis();
        this.usedWeight = 0;
    }

    public void acquire(int weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("weight must be positive");
        }
        while (true) {
            long sleepMillis = 0L;
            synchronized (this) {
                long now = System.currentTimeMillis();
                if (now - windowStartMillis >= 60_000L) {
                    windowStartMillis = now;
                    usedWeight = 0;
                }
                if (usedWeight + weight <= maxWeightPerMinute) {
                    usedWeight += weight;
                    return;
                }
                sleepMillis = (windowStartMillis + 60_000L) - now;
            }
            try {
                Thread.sleep(Math.max(0L, sleepMillis));
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Rate limiter interrupted", ex);
            }
        }
    }
}
