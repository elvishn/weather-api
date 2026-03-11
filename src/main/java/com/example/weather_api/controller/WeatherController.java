package com.example.weather_api.controller;

import com.example.weather_api.data.TemperatureResponse;
import com.example.weather_api.data.WeatherNowData;
import com.example.weather_api.service.WeatherService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import org.hibernate.validator.constraints.Range;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api")
public class WeatherController {
    private final WeatherService service;

    @GetMapping("/weather/current")
    public Mono<WeatherNowData> getWeatherData() {
        log.info("Request received for /weather/current");
        return service.getNowWeather();
    }

    @GetMapping("/weather/historical")
    public Mono<List<WeatherNowData>> getWeatherData24hours() {
        log.info("Request received for /weather/historical");
        return service.getWeather24hours();
    }

    @GetMapping("/weather/historical/max")
    public Mono<TemperatureResponse> getMaxTemperature() {
        log.info("Request received for /weather/historical/max");
        return service.getMaxTemperature();
    }

    @GetMapping("/weather/historical/min")
    public Mono<TemperatureResponse> getMinTemperature() {
        log.info("Request received for /weather/historical/min");
        return service.getMinTemperature();
    }

    @GetMapping("/weather/historical/avg")
    public Mono<TemperatureResponse> getAvgTemperature() {
        log.info("Request received for /weather/historical/avg");
        return service.getAvgTemperature();
    }

    @GetMapping("/weather/by_time")
    public Mono<WeatherNowData> getWeatherByTime(@RequestParam("hour") @Min(0) @Max(23) int hour) {
        log.info("Request received for /weather/by_time");
        return service.getWeatherByTime(hour)
                .flatMap(optional -> optional.map(Mono::just).orElseGet(Mono::empty))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Data for hour not found")));
    }

    @GetMapping("/health")
    @ResponseStatus(HttpStatus.OK)
    public String getOk() {
        log.info("Request received for /health (status for back)");
        return "OK";}

}
