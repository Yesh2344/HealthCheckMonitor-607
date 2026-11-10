package com.example.health.model;

import java.time.Instant;

/**
 * Immutable DTO that encapsulates the result of a single health check.
 */
public record HealthCheckResult(
        String url,
        ServiceStatus status,
        long responseTimeMs,
        String errorMessage,
        Instant timestamp) {

    public static HealthCheckResult up(String url, long responseTimeMs) {
        return new HealthCheckResult(url, ServiceStatus.UP, responseTimeMs, null, Instant.now());
    }

    public static HealthCheckResult down(String url, long responseTimeMs, String errorMessage) {
        return new HealthCheckResult(url, ServiceStatus.DOWN, responseTimeMs, errorMessage, Instant.now());
    }
}