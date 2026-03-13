package com.example.weather_api.data;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

@Data
public class WeatherNowData {
    private final String cityName = "Zermart";
    private final LocalDate date;
    private final LocalTime time;
    private final Double value; //температура
    private final String unit; //еденицы измерения
    private final String weatherText;
    private final String precipitationType; // тип осадков


    @Override
    public String toString() {
        return  "City: " + cityName + "\n" + "Date: " + date + "\n" + "Time: " + time + "\n" + "Temperature: " + value + " " + unit + "\n" + "Weather description: " + weatherText + "\n" + "Type precipitation: " + precipitationType;
    }

    public static  WeatherNowData toNowDto(JsonNode root) {
        JsonNode dto = root.isArray() ? root.get(0) : root;
        ZonedDateTime dateTime = ZonedDateTime.parse(dto.path("LocalObservationDateTime").asText());
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        String weatherText = dto.path("WeatherText").asText();
        String precipitationType = dto.path("PrecipitationType").asText(); // тип осадков
        Double value = dto.path("Temperature").path("Metric").path("Value").asDouble(); //температура
        String unit = dto.path("Temperature").path("Metric").path("Unit").asText();; //еденицы измерения
        if (precipitationType.equals("null")) {
            precipitationType = "There is no precipitation";
        }
        return new WeatherNowData(date, time, value, unit, weatherText, precipitationType);

    }
}
