package com.example.health.config;

import com.example.health.service.HealthCheckService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Web configuration that registers the health‑check router.
 */
@Configuration
public class WebConfig {

    @Bean
    public RouterFunction<ServerResponse> healthRouter(HealthCheckService healthCheckService) {
        return healthCheckService.router();
    }
}