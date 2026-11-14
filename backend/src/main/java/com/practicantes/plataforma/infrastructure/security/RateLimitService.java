package com.practicantes.plataforma.infrastructure.security;

import com.practicantes.plataforma.application.exception.TooManyRequestsException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RateLimitService {

    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final long requestsPerMinute;

    public RateLimitService(@Value("${app.security.rate-limit-per-minute}") long requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
    }

    public void check(String operation, String clientIp) {
        Bucket bucket = buckets.computeIfAbsent(operation + ":" + clientIp, ignored -> newBucket());
        if (!bucket.tryConsume(1)) {
            throw new TooManyRequestsException();
        }
    }

    private Bucket newBucket() {
        Bandwidth limit = Bandwidth.classic(
                requestsPerMinute, Refill.greedy(requestsPerMinute, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }
}
