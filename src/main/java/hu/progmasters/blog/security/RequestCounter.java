package hu.progmasters.blog.security;



import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

public class RequestCounter {
    private AtomicInteger count;

    @Getter
    private long creationTime;

    public RequestCounter() {
        this.count = new AtomicInteger(1);
        this.creationTime = System.currentTimeMillis();
    }

    public int incrementAndGet() {
        return count.incrementAndGet();
    }

    public void reset() {
        count.set(0);
        creationTime = System.currentTimeMillis();
    }
}