package com.aicoach.execution.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

/**
 * Configures a {@link RestClient} pre-wired with the Judge0 base URL and API
 * credentials read from environment variables.
 *
 * <p>Credentials are never returned via any API endpoint and are only used
 * within this backend process.
 */
@Configuration
public class Judge0Config {

    /**
     * Builds a {@link RestClient} configured for Judge0 API calls.
     *
     * <p>The RapidAPI key and host headers are added only when the values are
     * non-empty, so the same config works with both the self-hosted Community
     * Edition (no key required) and the RapidAPI-hosted edition.
     */
    @Bean
    public RestClient judge0RestClient(
            @Value("${app.judge0.base-url}") String baseUrl,
            @Value("${app.judge0.api-key:}") String apiKey,
            @Value("${app.judge0.api-host:}") String apiHost) {

        RestClient.Builder builder = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        if (apiKey != null && !apiKey.isBlank()) {
            builder.defaultHeader("X-RapidAPI-Key", apiKey);
        }
        if (apiHost != null && !apiHost.isBlank()) {
            builder.defaultHeader("X-RapidAPI-Host", apiHost);
        }

        return builder.build();
    }
}
