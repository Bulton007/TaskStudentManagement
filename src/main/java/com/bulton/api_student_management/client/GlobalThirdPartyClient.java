package com.bulton.api_student_management.client;

import com.bulton.api_student_management.config.ThirdPartyProperties;
import com.bulton.api_student_management.exception.ThirdPartyApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
@Slf4j
public class GlobalThirdPartyClient {

    private static final int MAX_LOG_BODY_LENGTH = 5000;

    private final ThirdPartyProperties properties;
    private final ObjectMapper objectMapper;

    /*
     * GET request
     */
    public <T> T get(
        String providerName,
        String path,
        Class<T> responseType
    ) {
        return execute(
            providerName,
            HttpMethod.GET,
            path,
            null,
            responseType
        );
    }

    /*
     * POST request
     */
    public <B, T> T post(
        String providerName,
        String path,
        B requestBody,
        Class<T> responseType
    ) {
        return execute(
            providerName,
            HttpMethod.POST,
            path,
            requestBody,
            responseType
        );
    }

    /*
     * PUT request
     */
    public <B, T> T put(
        String providerName,
        String path,
        B requestBody,
        Class<T> responseType
    ) {
        return execute(
            providerName,
            HttpMethod.PUT,
            path,
            requestBody,
            responseType
        );
    }

    /*
     * DELETE request
     */
    public <T> T delete(
        String providerName,
        String path,
        Class<T> responseType
    ) {
        return execute(
            providerName,
            HttpMethod.DELETE,
            path,
            null,
            responseType
        );
    }

    private <B, T> T execute(
        String providerName,
        HttpMethod method,
        String path,
        B requestBody,
        Class<T> responseType
    ) {
        ThirdPartyProperties.Provider provider =
            findProvider(providerName);
        log.warn(
    "TIMEOUT_TEST provider={} baseUrl={} configuredTimeout={}",
    providerName,
    provider.getBaseUrl(),
    provider.getTimeout()
);

        validatePath(path);

        WebClient webClient = WebClient.builder()
            .baseUrl(provider.getBaseUrl())
            .build();

        String requestBodyLog = toJsonForLog(requestBody);
        long startTime = System.currentTimeMillis();

        log.info(
            "THIRD_PARTY_REQUEST provider={} method={} baseUrl={} path={} timeout={} requestBody={}",
            providerName,
            method,
            provider.getBaseUrl(),
            path,
            provider.getTimeout(),
            requestBodyLog
        );

        try {
            WebClient.RequestBodySpec request =
                webClient
                    .method(method)
                    .uri(path)
                    .accept(MediaType.APPLICATION_JSON);

            WebClient.RequestHeadersSpec<?> requestSpec;

            if (requestBody != null) {
                requestSpec = request
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody);
            } else {
                requestSpec = request;
            }

            return requestSpec
                .exchangeToMono(response -> {
                    HttpStatusCode status =
                        response.statusCode();

                    return response
                        .bodyToMono(String.class)
                        .defaultIfEmpty("")
                        .flatMap(responseBody -> {
                            long duration =
                                System.currentTimeMillis()
                                    - startTime;

                            if (status.isError()) {
                                log.error(
                                    "THIRD_PARTY_HTTP_ERROR provider={} method={} path={} status={} durationMs={} responseBody={}",
                                    providerName,
                                    method,
                                    path,
                                    status.value(),
                                    duration,
                                    limit(responseBody)
                                );

                                return Mono.error(
                                    new ThirdPartyApiException(
                                        "Third-party provider "
                                            + providerName
                                            + " returned HTTP "
                                            + status.value()
                                    )
                                );
                            }

                            log.info(
                                "THIRD_PARTY_RESPONSE provider={} method={} path={} status={} durationMs={} responseBody={}",
                                providerName,
                                method,
                                path,
                                status.value(),
                                duration,
                                limit(responseBody)
                            );

                            return convertResponse(
                                responseBody,
                                responseType,
                                providerName
                            );
                        });
                })
                .timeout(provider.getTimeout())
                .onErrorMap(
                    TimeoutException.class,
                    exception -> {
                        log.error(
                            "THIRD_PARTY_TIMEOUT provider={} method={} path={} timeout={}",
                            providerName,
                            method,
                            path,
                            provider.getTimeout(),
                            exception
                        );

                        return new ThirdPartyApiException(
                            "Third-party request timed out: "
                                + providerName,
                            exception
                        );
                    }
                )
                .onErrorMap(
                    WebClientRequestException.class,
                    exception -> {
                        log.error(
                            "THIRD_PARTY_CONNECTION_ERROR provider={} method={} path={} message={}",
                            providerName,
                            method,
                            path,
                            exception.getMessage(),
                            exception
                        );

                        return new ThirdPartyApiException(
                            "Could not connect to third-party provider: "
                                + providerName,
                            exception
                        );
                    }
                )
                .onErrorMap(
                    WebClientResponseException.class,
                    exception -> {
                        log.error(
                            "THIRD_PARTY_RESPONSE_ERROR provider={} method={} path={} status={} responseBody={}",
                            providerName,
                            method,
                            path,
                            exception.getStatusCode().value(),
                            limit(
                                exception.getResponseBodyAsString()
                            ),
                            exception
                        );

                        return new ThirdPartyApiException(
                            "Third-party provider "
                                + providerName
                                + " returned HTTP "
                                + exception.getStatusCode().value(),
                            exception
                        );
                    }
                )
                .block();

        } catch (ThirdPartyApiException exception) {
            throw exception;

        } catch (Exception exception) {
            log.error(
                "THIRD_PARTY_UNEXPECTED_ERROR provider={} method={} path={}",
                providerName,
                method,
                path,
                exception
            );

            throw new ThirdPartyApiException(
                "Unexpected third-party API error: "
                    + providerName,
                exception
            );
        }
    }

    private ThirdPartyProperties.Provider findProvider(
        String providerName
    ) {
        ThirdPartyProperties.Provider provider =
            properties
                .getProviders()
                .get(providerName);

        if (provider == null) {
            throw new ThirdPartyApiException(
                "Unknown third-party provider: "
                    + providerName
            );
        }

        if (
            provider.getBaseUrl() == null
                || provider.getBaseUrl().isBlank()
        ) {
            throw new ThirdPartyApiException(
                "Base URL is missing for provider: "
                    + providerName
            );
        }

        return provider;
    }

    private void validatePath(String path) {
        if (
            path == null
                || path.isBlank()
                || !path.startsWith("/")
        ) {
            throw new IllegalArgumentException(
                "Third-party path must start with /"
            );
        }

        /*
         * Prevent callers from overriding the approved base URL.
         */
        if (
            path.startsWith("//")
                || path.contains("://")
        ) {
            throw new IllegalArgumentException(
                "Absolute third-party URLs are not allowed"
            );
        }
    }

    private <T> Mono<T> convertResponse(
        String responseBody,
        Class<T> responseType,
        String providerName
    ) {
        if (responseType == Void.class) {
            return Mono.empty();
        }

        if (
            responseBody == null
                || responseBody.isBlank()
        ) {
            return Mono.error(
                new ThirdPartyApiException(
                    "Third-party provider returned an empty response: "
                        + providerName
                )
            );
        }

        try {
            T convertedResponse = objectMapper.readValue(
                responseBody,
                responseType
            );

            return Mono.just(convertedResponse);

        } catch (Exception exception) {
            log.error(
                "THIRD_PARTY_MAPPING_ERROR provider={} responseBody={}",
                providerName,
                limit(responseBody),
                exception
            );

            return Mono.error(
                new ThirdPartyApiException(
                    "Could not convert response from provider: "
                        + providerName,
                    exception
                )
            );
        }
    }

    private String toJsonForLog(Object body) {
        if (body == null) {
            return "";
        }

        try {
            return limit(
                objectMapper.writeValueAsString(body)
            );
        } catch (Exception exception) {
            return "[UNABLE_TO_SERIALIZE]";
        }
    }

    private String limit(String value) {
        if (value == null) {
            return "";
        }

        String singleLine = value
            .replace("\n", "")
            .replace("\r", "");

        if (
            singleLine.length()
                <= MAX_LOG_BODY_LENGTH
        ) {
            return singleLine;
        }

        return singleLine.substring(
            0,
            MAX_LOG_BODY_LENGTH
        ) + "...[TRUNCATED]";
    }
}