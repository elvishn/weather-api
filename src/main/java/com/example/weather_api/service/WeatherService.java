package com.example.weather_api.service;

import com.example.weather_api.data.WeatherNowData;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
@AllArgsConstructor
public class WeatherService {
    private final WebClient webClient;
    static final String CITY_URL_TEMPLATE = "/locations/v1/cities/search?q=Zermatt";
    static final String NOW_URL_TEMPLATE = "/currentconditions/v1/315560";

    public  Mono<String> cityCode() {
        return webClient.get()
                .uri(CITY_URL_TEMPLATE)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(jsonNode -> {
                    String code = jsonNode.get(0).path("Key").asText(null);
                    return code;
                });
    }

    public Mono<WeatherNowData> getNowWeather() {
        return webClient.get()
                .uri(NOW_URL_TEMPLATE)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(jsonNode -> WeatherNowData.toNowDto(jsonNode));
    }
}
