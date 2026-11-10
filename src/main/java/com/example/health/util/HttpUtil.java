package com.example.health.util;

import java.io.IOException;
import java.net.URI;
// minor polish
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Utility class that performs a simple HTTP GET request using the
 * {@link java.net.http.HttpClient} introduced in Java 11.
 */
public final class HttpUtil {

    private HttpUtil() {
        // Utility class – prevent instantiation
    }

    /**
     * Executes a GET request against the supplied {@code uri} respecting the given {@code timeout}.
     *
     * @param uri     target endpoint
     * @param timeout request timeout
     * @return the HTTP status code
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public static int get(URI uri, Duration timeout) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(timeout)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .timeout(timeout)
                .header("Accept", "application/json")
                .build();

        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        return response.statusCode();
    }
}