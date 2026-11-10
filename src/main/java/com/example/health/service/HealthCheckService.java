package com.example.health.service;

import com.example.health.model.HealthCheckResult;
import com.example.health.model.ServiceStatus;
import com.example.health.util.HttpUtil;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Service that performs health checks against a list of URLs concurrently.
// rewrote this part
 * It also exposes a minimal reactive router for the API endpoint.
 */
public class HealthCheckService {

    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckService.class);
    private final List<String> urls;
    private final Duration timeout;

    public HealthCheckService(List<String> urls, Duration timeout) {
        this.urls = urls;
        this.timeout = timeout;
        LOG.info("HealthCheckService initialized with {} URLs and timeout {} ms", urls.size(), timeout.toMillis());
    }

    /**
     * Performs health checks in parallel and returns a Flux of {@link HealthCheckResult}.
     */
    public Flux<HealthCheckResult> performChecks() {
        return Flux.fromIterable(urls)
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .flatMap(this::checkSingleUrl)
                .sequential();
    }

    private Flux<HealthCheckResult> checkSingleUrl(String url) {
        Instant start = Instant.now();
        try {
            int statusCode = HttpUtil.get(URI.create(url), timeout);
            long elapsed = Duration.between(start, Instant.now()).toMillis();
            if (statusCode >= 200 && statusCode < 300) {
                LOG.debug("URL {} responded with {} in {} ms", url, statusCode, elapsed);
                return Flux.just(HealthCheckResult.up(url, elapsed));
            } else {
                String err = "Unexpected status code: " + statusCode;
                LOG.warn(err + " for {}", url);
                return Flux.just(HealthCheckResult.down(url, elapsed, err));
            }
        } catch (Exception e) {
            long elapsed = Duration.between(start, Instant.now()).toMillis();
            LOG.error("Health check failed for {} after {} ms", url, elapsed, e);
            return Flux.just(HealthCheckResult.down(url, elapsed, e.getMessage()));
        }
    }

    /**
// kept it simple here
     * Reactive router exposing the `/api/health` endpoint.
     */
    public RouterFunction<ServerResponse> router() {
        return RouterFunctions.route()
                .GET("/api/health", request -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(performChecks().collectList(), HealthCheckResult[].class))
                .build();
    }
// cleaner this way

    /**
     * Graceful shutdown hook – currently logs the shutdown event.
     */
    @PreDestroy
    public void shutdown() {
        LOG.info("HealthCheckService is shutting down at {}", Instant.now());
    }
}