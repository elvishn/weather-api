package com.example.weather_api.controller;

import com.example.weather_api.data.WeatherNowData;
import com.example.weather_api.service.WeatherService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import org.hibernate.validator.constraints.Range;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api")
public class WeatherController {
    private final WeatherService service;

    @GetMapping("/weather/current")
    public Mono<WeatherNowData> getWeatherData() {
        return service.getNowWeather();
    }

    @GetMapping("/weather/historical")
    public Mono<List<WeatherNowData>> getWeatherData24hours() { return service.getWeather24hours();}

    @GetMapping("/weather/historical/max")
    public Mono<Double> getMaxTemperature() {return service.getMaxTemperature();}

    @GetMapping("/weather/historical/min")
    public Mono<Double> getMinTemperature() {return service.getMinTemperature();}

    @GetMapping("/weather/historical/avg")
    public Mono<Double> getAvgTemperature() {return service.getAvgTemperature();}

    @GetMapping("/weather/by_time")
    public Mono<WeatherNowData> getWeatherByTime(@RequestParam("hour") @Min(0) @Max(23) int hour) {
        return service.getWeatherByTime(hour)
                .flatMap(optional -> optional.map(Mono::just).orElseGet(Mono::empty))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Data for hour not found")));
    }

    @GetMapping("/health")
    @ResponseStatus(HttpStatus.OK)
    public String getOk() {return "OK";}

}
