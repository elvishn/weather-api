package com.example.weather_api.controller;

import com.example.weather_api.data.TemperatureResponse;
import com.example.weather_api.data.WeatherNowData;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import okhttp3.mockwebserver.MockWebServer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import java.io.IOException;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WeatherControllerTest {
    private final String weatherHistorical = """
                [
                                {
                                    "LocalObservationDateTime": "2024-08-25T00:00:00Z",
                                    "WeatherText": "Night cloudy",
                                    "PrecipitationType": "Rain",
                                    "Temperature": {
                                        "Metric": {
                                            "Value": 18.5,
                                            "Unit": "C"
                                        }
                                    }
                                },
                                {
                                    "LocalObservationDateTime": "2024-08-25T01:00:00Z",
                                    "WeatherText": "Cloudy",
                                    "PrecipitationType": "Rain",
                                    "Temperature": {
                                        "Metric": {
                                            "Value": 18.5,
                                            "Unit": "C"
                                        }
                                    }
                                },
                                {
                                    "LocalObservationDateTime": "2024-08-25T02:00:00Z",
                                    "WeatherText": "Light Rain",
                                    "PrecipitationType": "Rain",
                                    "Temperature": {
                                        "Metric": {
                                            "Value": 19.0,
                                            "Unit": "C"
                                        }
                                    }
                                }
                            ]""";
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static MockWebServer mockWebServer;

    @BeforeAll
    static void setUpServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDownServer() throws IOException {
        mockWebServer.shutdown();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        // Подменяем URL внешнего API на наш MockWebServer
        registry.add("accuweather.api.base-url", () ->
                String.format("http://localhost:%s", mockWebServer.getPort()));
    }

    @Test
    void getWeatherCurrent_ShouldReturnWeatherData() throws Exception {
        String accuWeatherResponse = """
                [
                    {
                        "LocalObservationDateTime": "2024-08-25T15:00:00Z",
                        "WeatherText": "Sunny",
                        "PrecipitationType": "Rain",
                        "Temperature": {
                            "Metric": {
                                "Value": 25.5,
                                "Unit": "C"
                            }
                        }
                    }
                ]
                """;

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(accuWeatherResponse));

        String url = "http://localhost:" + port + "/api/weather/current";
        ResponseEntity<WeatherNowData> response = restTemplate.getForEntity(
                url,
                WeatherNowData.class
        );

        assertThat(response.getStatusCode().value()).isEqualTo(200);

        WeatherNowData weatherData = response.getBody();
        assertThat(weatherData).isNotNull();
        assertThat(weatherData.getDate()).isEqualTo("2024-08-25");
        assertThat(weatherData.getTime()).isEqualTo("15:00:00");
        assertThat(weatherData.getValue()).isEqualTo(25.5);
        assertThat(weatherData.getUnit()).isEqualTo("C");
        assertThat(weatherData.getWeatherText()).isEqualTo("Sunny");
        assertThat(weatherData.getPrecipitationType()).isEqualTo("Rain");
        assertThat(mockWebServer.getRequestCount()).isEqualTo(1);
    }

    @Test
    void getWeatherData24_ShouldReturnListWeatherData() throws Exception{

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(weatherHistorical));

        String url = "http://localhost:" + port + "/api/weather/historical";
        ResponseEntity<List<WeatherNowData>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<WeatherNowData>>() {});

        assertThat(response.getStatusCode().value()).isEqualTo(200);

        List<WeatherNowData> weatherList = response.getBody();
        assertThat(weatherList).isNotNull();
        assertThat(weatherList).hasSize(3);
        WeatherNowData first = weatherList.get(0);
        assertThat(first).isNotNull();
        assertThat(first.getDate()).isEqualTo("2024-08-25");
        assertThat(first.getTime()).isEqualTo("00:00:00");
        assertThat(first.getValue()).isEqualTo(18.5);
        assertThat(first.getUnit()).isEqualTo("C");
        assertThat(first.getWeatherText()).isEqualTo("Night cloudy");
        assertThat(first.getPrecipitationType()).isEqualTo("Rain");
        WeatherNowData second = weatherList.get(1);
        assertThat(second).isNotNull();
        assertThat(second.getDate()).isEqualTo("2024-08-25");
        assertThat(second.getTime()).isEqualTo("01:00:00");
        assertThat(second.getValue()).isEqualTo(18.5);
        assertThat(second.getUnit()).isEqualTo("C");
        assertThat(second.getWeatherText()).isEqualTo("Cloudy");
        assertThat(second.getPrecipitationType()).isEqualTo("Rain");
        WeatherNowData third = weatherList.get(2);
        assertThat(third).isNotNull();
        assertThat(third.getDate()).isEqualTo("2024-08-25");
        assertThat(third.getTime()).isEqualTo("02:00:00");
        assertThat(third.getValue()).isEqualTo(19.0);
        assertThat(third.getUnit()).isEqualTo("C");
        assertThat(third.getWeatherText()).isEqualTo("Light Rain");
        assertThat(third.getPrecipitationType()).isEqualTo("Rain");

        assertThat(mockWebServer.getRequestCount()).isEqualTo(1);

    }

    @Test
    void getMaxTemperature_shouldReturnTemperatureResponse() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(weatherHistorical));

        String url = "http://localhost:" + port + "/api/weather/historical/max";

        ResponseEntity<TemperatureResponse> response = restTemplate.getForEntity(
                url,
                TemperatureResponse.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);

        TemperatureResponse temp = response.getBody();
        assertThat(temp).isNotNull();
        assertThat(temp.temperature()).isEqualTo(19.0);
        assertThat(temp.units()).isEqualTo("C");

        assertThat(mockWebServer.getRequestCount()).isEqualTo(1);
    }

    @Test
    void getMinTemperature_shouldReturnTemperatureResponse() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(weatherHistorical));

        String url = "http://localhost:" + port + "/api/weather/historical/min";

        ResponseEntity<TemperatureResponse> response = restTemplate.getForEntity(
                url,
                TemperatureResponse.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);

        TemperatureResponse temp = response.getBody();
        assertThat(temp).isNotNull();
        assertThat(temp.temperature()).isEqualTo(18.5);
        assertThat(temp.units()).isEqualTo("C");

        assertThat(mockWebServer.getRequestCount()).isEqualTo(1);
    }

    @Test
    void getAvgTemperature_shouldReturnTemperatureResponse() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(weatherHistorical));

        String url = "http://localhost:" + port + "/api/weather/historical/avg";

        ResponseEntity<TemperatureResponse> response = restTemplate.getForEntity(
                url,
                TemperatureResponse.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);

        TemperatureResponse temp = response.getBody();
        assertThat(temp).isNotNull();
        assertThat(temp.temperature()).isEqualTo(18.67);
        assertThat(temp.units()).isEqualTo("C");

        assertThat(mockWebServer.getRequestCount()).isEqualTo(1);
    }

    @Test
    void getWeatherByTime_shouldReturnWeatherNowData() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(weatherHistorical));

        int hour = 2;
        String url = "http://localhost:" + port + "/api/weather/by_time?hour=" + hour;

        ResponseEntity<WeatherNowData> response = restTemplate.getForEntity(
                url,
                WeatherNowData.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);

        WeatherNowData third = response.getBody();
        assertThat(third).isNotNull();
        assertThat(third.getDate()).isEqualTo("2024-08-25");
        assertThat(third.getTime()).isEqualTo("02:00:00");
        assertThat(third.getValue()).isEqualTo(19.0);
        assertThat(third.getUnit()).isEqualTo("C");
        assertThat(third.getWeatherText()).isEqualTo("Light Rain");
        assertThat(third.getPrecipitationType()).isEqualTo("Rain");

        assertThat(mockWebServer.getRequestCount()).isEqualTo(1);
    }

    @Test
    void getWeatherByTime24_should400() throws Exception {
        int hour = 24;
        String url = "http://localhost:" + port + "/api/weather/by_time?hour=" + hour;

        ResponseEntity<String> response = restTemplate.getForEntity(
                url,
                String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void getWeatherByTime_should400() throws Exception {
        int hour = -1;
        String url = "http://localhost:" + port + "/api/weather/by_time?hour=" + hour;

        ResponseEntity<String> response = restTemplate.getForEntity(
                url,
                String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }
}
