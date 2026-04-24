package uk.ac.soton.comp2300.group42.energyclient.domain.model;

public record WeatherEntry(
        double latitude,
        double longitude,
        CurrentWeather current,
        HourlyWeather hourly
) {
}
