package com.bulton.api_student_management.service.impl;

import com.bulton.api_student_management.dto.response.ExternalPostResponse;
import com.bulton.api_student_management.exception.ResourceNotFoundException;
import com.bulton.api_student_management.exception.ThirdPartyApiException;
import com.bulton.api_student_management.service.ExternalPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalPostServiceImpl implements ExternalPostService {

    private final WebClient jsonPlaceholderWebClient;

    @Override
    public ExternalPostResponse getPostById(Long id) {
        return jsonPlaceholderWebClient
            .get()
            .uri("/posts/{id}", id)
            .retrieve()

            .onStatus(
                status -> status.value() == 404,
                response -> {
                    log.warn(
                        "THIRD_PARTY_NOT_FOUND postId={}",
                        id
                    );

                    return Mono.error(
                        new ResourceNotFoundException(
                            "External post not found with ID: " + id
                        )
                    );
                }
            )

            .onStatus(
                HttpStatusCode::isError,
                response -> response
                    .bodyToMono(String.class)
                    .defaultIfEmpty("")
                    .flatMap(body -> {
                        log.error(
                            "THIRD_PARTY_ERROR status={} body={}",
                            response.statusCode().value(),
                            body
                        );

                        return Mono.error(
                            new ThirdPartyApiException(
                                "Third-party API returned status: "
                                    + response.statusCode().value()
                            )
                        );
                    })
            )

            .bodyToMono(ExternalPostResponse.class)

            .doOnNext(post ->
                log.info(
                    "THIRD_PARTY_RESPONSE_BODY post={}",
                    post
                )
            )

            .timeout(Duration.ofSeconds(5))

            .onErrorMap(
                TimeoutException.class,
                exception -> new ThirdPartyApiException(
                    "Third-party API request timed out",
                    exception
                )
            )

            .onErrorMap(
                WebClientRequestException.class,
                exception -> new ThirdPartyApiException(
                    "Could not connect to third-party API",
                    exception
                )
            )

            .block();
    }
}