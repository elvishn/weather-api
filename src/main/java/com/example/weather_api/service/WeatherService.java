package com.example.weather_api.service;

import com.example.weather_api.data.WeatherNowData;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Service
@AllArgsConstructor
public class WeatherService {
    private final WebClient webClient;
    static final String NOW_URL_TEMPLATE = "/currentconditions/v1/315560"; // weather in Zemmart


    public Mono<WeatherNowData> getNowWeather() {
        return webClient.get()
                .uri(NOW_URL_TEMPLATE)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(jsonNode -> WeatherNowData.toNowDto(jsonNode));
    }

    public List<WeatherNowData> getWeather24hours() {
        JsonNode list = webClient.get()
                .uri(NOW_URL_TEMPLATE + "/historical/24")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        return StreamSupport.stream(list.spliterator(), false)
                .map(WeatherNowData::toNowDto)
                .collect(Collectors.toList());
    }

    public Mono<List<Double>> getTemperatureList() {
        return webClient.get()
                .uri(NOW_URL_TEMPLATE + "/historical/24")
                .retrieve()
                .bodyToFlux(JsonNode.class)  // Получаем Flux из 24 элементов
                .map(node -> node.path("Temperature")
                        .path("Metric")
                        .path("Value")
                        .asDouble())
                .collectList();
    }

    public Mono<Double> getMaxTemperature() {
        return getTemperatureList().map(list -> Collections.max(list));
    }

    public Mono<Double> getMinTemperature() {
        return getTemperatureList().map(list -> Collections.min(list));
    }

    public Mono<Double> getAvgTemperature() {
        return getTemperatureList()
                        .map(list -> {
                            double avg = list.stream()
                                    .mapToDouble(Double::doubleValue)
                                    .average()
                                    .orElse(0.0);
                        return Math.round(avg * 100) / 100.0;
                        });
    }

    public Optional<WeatherNowData> getWeatherByTime(Integer num) {
        return getWeather24hours().stream()
                .filter(dto -> {
                    return dto.getTime().getHour() == num;
                })
                .findFirst();
    }
}
