package com.yasirkhan.em.clients;

import com.yasirkhan.em.dtos.BenchmarkResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SalaryBenchmarkClient {

    private static final Logger log =
            LoggerFactory.getLogger(SalaryBenchmarkClient.class);

    private final RestTemplate restTemplate;

    public SalaryBenchmarkClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Resilience4j annotation sequence Retry ( CircuitBreaker ( RateLimiter ( TimeLimiter ( Bulkhead ( Method ) ) ) ) )
    @CircuitBreaker(name = "salaryBenchmark")                                // no fallback — just observes and records
    @Retry(name = "salaryBenchmark", fallbackMethod = "benchmarkFallback")   // ✅ the ONLY fallback — true outermost
//    @RateLimiter(name = "salaryBenchmark")                                   // no fallback — just gates, lets exceptions through
    public BenchmarkResponse getBenchmark(String department) {

        log.info("Calling benchmark service for {}", department);

        BenchmarkResponse res = restTemplate.getForObject(
                "http://localhost:8081/benchmark/{dept}",
                BenchmarkResponse.class,
                department
        );

        return new BenchmarkResponse(
                res.department(),
                res.averageSalary(),
                "LIVE"
        );
    }

    /*
        * Same params + Throwable (or a specific exception type)
        * NOTE: this now runs only after ALL retry attempts are exhausted,
        * or immediately if the circuit breaker rejects the call (OPEN state).
     */
    private BenchmarkResponse benchmarkFallback(
            String department,
            Throwable ex
    ) {
        log.warn(
                "Fallback for {} because {}: {}",
                department,
                ex.getClass().getSimpleName(),
                ex.getMessage()
        );

        return new BenchmarkResponse(
                department,
                null,
                "FALLBACK"
        );
    }
}


/*
Each failed call triggered a retry, and that retry got blocked by your rate limiter
(since all 3 permits were already spent) — so every request ended up waiting long enough to count as
"slow," which is what tripped the circuit breaker open.
 */