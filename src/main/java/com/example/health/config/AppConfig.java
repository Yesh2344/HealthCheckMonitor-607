package com.example.health.config;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.health.service.HealthCheckService;

/**
 * Central configuration class that creates beans and parses
 * application properties.
 */
@Configuration
public class AppConfig {

    @Value("${healthcheck.urls}")
    private String urls;

    @Value("${healthcheck.timeoutMs}")
    private long timeoutMs;

    /**
     * Parses the comma‑separated URL list into an immutable List.
     */
    @Bean
    public List<String> healthCheckUrls() {
        return Stream.of(urls.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Provides the request timeout as a {@link Duration}.
     */
    @Bean
    public Duration requestTimeout() {
        return Duration.ofMillis(timeoutMs);
    }

    /**
     * Core service bean.
     */
    @Bean
    public HealthCheckService healthCheckService(List<String> healthCheckUrls, Duration requestTimeout) {
        return new HealthCheckService(healthCheckUrls, requestTimeout);
    }
}