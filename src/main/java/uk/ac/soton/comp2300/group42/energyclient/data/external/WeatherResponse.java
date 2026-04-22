package uk.ac.soton.comp2300.group42.energyclient.data.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WeatherResponse(
        @JsonProperty("latitude")
        double latitude,

        @JsonProperty("longitude")
        double longitude,

        @JsonProperty("current")
        CurrentWeather current,

        @JsonProperty("hourly")
        HourlyWeather hourly
) {}


