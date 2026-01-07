package com.github.jhinslog.onpopserver.place.service;

import com.github.jhinslog.onpopserver.place.dto.seoul.SeoulApiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeoulApiService {

    private final WebClient.Builder webClientBuilder;

    @Value("${seoul.api.key}")
    private String apiKey;

    @Value("${seoul.api.url}")
    private String baseUrl;

    @Value("${seoul.api.type}")
    private String type;

    @Value("${seoul.api.service}")
    private String serviceName;

    @Value("${seoul.api.start-index}")
    private int startIndex;

    @Value("${seoul.api.end-index}")
    private int endIndex;

    public SeoulApiDto fetchCityData(String areaName) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("서울시 API 키가 설정되지 않았습니다.");
        }

        URI uri = UriComponentsBuilder.fromUriString(String.valueOf(baseUrl))
                .pathSegment(apiKey, type, serviceName, String.valueOf(startIndex), String.valueOf(endIndex), areaName)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();

        log.info("Fetching data from Seoul API: {}", areaName);
        log.debug("Request URI: {}", uri);

        try {
            return webClientBuilder.build()
                    .get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(SeoulApiDto.class)
                    .block();
        } catch (Exception e) {
            log.error("Failed to fetch data for area: {}. error: {}", areaName, e.getMessage());
            return null;
        }

    }

}
