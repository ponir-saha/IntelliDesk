package com.intellidesk.tool.service;

import com.intellidesk.tool.dto.ApiToolRequest;
import com.intellidesk.tool.dto.ApiToolResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiToolService {

    private final WebClient.Builder webClientBuilder;

    public ApiToolResponse invokeApi(ApiToolRequest request) {
        try {
            WebClient webClient = webClientBuilder.build();

            // Build headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (request.getHeaders() != null) {
                request.getHeaders().forEach(headers::add);
            }

            // Determine HTTP method
            HttpMethod method = HttpMethod.valueOf(request.getMethod().toUpperCase());

            // Make request
            Object response = webClient.method(method)
                    .uri(request.getUrl())
                    .headers(h -> h.addAll(headers))
                    .bodyValue(request.getBody() != null ? request.getBody() : "")
                    .retrieve()
                    .bodyToMono(Object.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();

            log.info("API call to {} completed successfully", request.getUrl());
            return new ApiToolResponse(true, 200, "API call successful", response);

        } catch (Exception e) {
            log.error("Error invoking API: {}", request.getUrl(), e);
            return new ApiToolResponse(false, 500, "Error: " + e.getMessage(), null);
        }
    }
}
