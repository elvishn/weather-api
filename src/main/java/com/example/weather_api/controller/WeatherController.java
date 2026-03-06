package com.example.weather_api.controller;

import com.example.weather_api.data.WeatherNowData;
import com.example.weather_api.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/weather")
public class WeatherController {
    private final WeatherService service;

    @GetMapping("/current")
    public Mono<WeatherNowData> getWeatherData() {
        return service.getNowWeather();
    }
}
