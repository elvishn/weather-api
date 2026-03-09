package com.example.weather_api.controller;

import com.example.weather_api.data.WeatherNowData;
import com.example.weather_api.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import org.hibernate.validator.constraints.Range;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WeatherController {
    private final WeatherService service;

    @GetMapping("/weather/current")
    public Mono<WeatherNowData> getWeatherData() {
        return service.getNowWeather();
    }

    @GetMapping("/weather/historical")
    public List<WeatherNowData> getWeatherData24hours() { return service.getWeather24hours();}

    @GetMapping("/weather/historical/max")
    public Double getMaxTemperature() {return service.getMaxTemperature();}

    @GetMapping("/weather/historical/min")
    public Double getMinTemperature() {return service.getMinTemperature();}

    @GetMapping("/weather/historical/avg")
    public Double getAvgTemperature() {return service.getAvgTemperature();}

    @GetMapping("/weather/by_time")
    public Optional<WeatherNowData> getWeatherByTime(@RequestParam
                                                     @Range(min=0, max=23, message="Hour should be between 0 and 23")
                                                     int hour) {
        return service.getWeatherByTime(hour);
    }

    @GetMapping("/health")
    @ResponseStatus(HttpStatus.OK)
    public String getOk() {return "OK";}

}
