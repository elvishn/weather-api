package com.example.weather_api.service;

import com.example.weather_api.data.TemperatureResponse;
import com.example.weather_api.data.WeatherNowData;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


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

    public Mono<List<WeatherNowData>> getWeather24hours() {
        return webClient.get()
                .uri(NOW_URL_TEMPLATE + "/historical/24")
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .map(WeatherNowData::toNowDto)
                .collect(Collectors.toList());
    }

    public Mono<List<Double>> getTemperatureList() {
        return getWeather24hours()
                .map(data -> data.stream()
                            .map(WeatherNowData::getValue)
                        .toList());
    }

    public Mono<TemperatureResponse> getMaxTemperature() {
        return getTemperatureList()
                .map(list -> Collections.max(list))
                .map(maxTemp -> new TemperatureResponse(maxTemp, "C"));
    }

    public Mono<TemperatureResponse> getMinTemperature() {
        return getTemperatureList()
                .map(list -> Collections.min(list))
                .map(minTemp -> new TemperatureResponse(minTemp, "C"));
    }

    public Mono<TemperatureResponse> getAvgTemperature() {
        return getTemperatureList()
                        .map(list -> {
                            double avg = list.stream()
                                    .mapToDouble(Double::doubleValue)
                                    .average()
                                    .orElse(0.0);
                        return Math.round(avg * 100) / 100.0;
                        })
                .map(avgTemp -> new TemperatureResponse(avgTemp, "C"));
    }

    public Mono<Optional<WeatherNowData>> getWeatherByTime(Integer num) {
        return getWeather24hours()
                .map(list ->
                        list.stream()
                                .filter(dto -> dto.getTime().getHour() == num)
                                .findFirst());

    }
}
