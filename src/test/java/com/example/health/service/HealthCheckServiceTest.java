package com.example.health.service;

import com.example.health.model.HealthCheckResult;
import com.example.health.model.ServiceStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

/**
 * Unit tests for {@link HealthCheckService}.
 * Uses a mock HTTP server (via WireMock) to simulate reachable and failing endpoints.
 */
class HealthCheckServiceTest {

    private static final String GOOD_URL = "http://localhost:8089/good";
    private static final String BAD_URL = "http://localhost:8089/bad";

    private HealthCheckService service;

    @BeforeEach
    void setUp() {
        // WireMock setup
        com.github.tomakehurst.wiremock.WireMockServer wireMock = new com.github.tomakehurst.wiremock.WireMockServer(8089);
        wireMock.start();
        wireMock.stubFor(com.github.tomakehurst.wiremock.client.WireMock.get("/good")
                .willReturn(com.github.tomakehurst.wiremock.client.WireMock.aResponse().withStatus(200)));
        wireMock.stubFor(com.github.tomakehurst.wiremock.client.WireMock.get("/bad")
                .willReturn(com.github.tomakehurst.wiremock.client.WireMock.aResponse().withFixedDelay(3000).withStatus(500)));
        service = new HealthCheckService(List.of(GOOD_URL, BAD_URL), Duration.ofMillis(2000));
    }

    @Test
    void testPerformChecks() {
        Flux<HealthCheckResult> results = service.performChecks();

        StepVerifier.create(results)
                .recordWith(ArrayList::new)
                .expectNextCount(2)
                .consumeRecordedWith(list -> {
                    HealthCheckResult good = list.stream()
                            .filter(r -> r.url().equals(GOOD_URL))
                            .findFirst()
                            .orElseThrow();
                    HealthCheckResult bad = list.stream()
                            .filter(r -> r.url().equals(BAD_URL))
                            .findFirst()
                            .orElseThrow();

                    assert good.status() == ServiceStatus.UP;
                    assert good.errorMessage() == null;

                    assert bad.status() == ServiceStatus.DOWN;
                    assert bad.errorMessage() != null;
                })
                .verifyComplete();
    }
}