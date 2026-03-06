package com.example.weather_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;


@Configuration
public class WeatherApiConfig {

    @Value("${accuweather.api.base-url}")
    private String baseUrl;

    @Value("${accuweather.api.key}")
    private String apiKey;

    @Bean
    public WebClient accuWeatherWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION,"Bearer " + apiKey)
                .defaultHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate")
                .build();
    }
}
