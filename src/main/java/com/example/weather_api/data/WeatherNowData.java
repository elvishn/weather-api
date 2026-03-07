package com.example.weather_api.data;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class WeatherNowData {
    private String cityName = "Zemmart";
    private final String date;
    private final String time;
    private final Double value; //температура
    private final String unit; //еденицы измерения
    private final String weatherText;
    private final String precipitationType; // тип осадков


    @Override
    public String toString() {
        return  "City: " + cityName + "\n" + "Date: " + cityName + "\n" + "Temperature: " + value + " " + unit + "\n" + "Weather description: " + weatherText + "\n" + "Type precipitation: " + precipitationType;
    }

    public static  WeatherNowData toNowDto(JsonNode root) {
        JsonNode dto = root.isArray() ? root.get(0) : root;
        List<String> dateTime = toDateAndTime(dto.path("LocalObservationDateTime").asText());
        String date = dateTime.get(0);
        String time = dateTime.get(1);
        String weatherText = dto.path("WeatherText").asText();
        String precipitationType = dto.path("PrecipitationType").asText(); // тип осадков
        Double value = dto.path("Temperature").path("Metric").path("Value").asDouble(); //температура
        String unit = dto.path("Temperature").path("Metric").path("Unit").asText();; //еденицы измерения
        if (precipitationType.equals("null")) {
            precipitationType = "There is no precipitation";
        }
        return new WeatherNowData(date, time, value, unit, weatherText, precipitationType);

    }

    private static List<String> toDateAndTime(String s) {
        List<String> res = List.of(s.split("T"));
        String date = res.get(0);
        String time = ((String)res.get(1)).substring(0, 8);
        return Arrays.asList(date, time);
    }
}
