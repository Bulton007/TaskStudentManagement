package com.bulton.api_student_management.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class WebClientConfig {

    @Bean
public WebClient jsonPlaceholderWebClient(
    @Value("${third-party.json-placeholder.base-url}") String baseUrl
) {
    return WebClient.builder()
        .baseUrl(baseUrl)
        .filter(logRequest())
        .filter(logResponse())
        .build();
}

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            log.info(
                "THIRD_PARTY_REQUEST method={} url={}",
                request.method(),
                request.url()
            );

            return Mono.just(request);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            log.info(
                "THIRD_PARTY_RESPONSE status={}",
                response.statusCode().value()
            );

            return Mono.just(response);
        });
    }
}