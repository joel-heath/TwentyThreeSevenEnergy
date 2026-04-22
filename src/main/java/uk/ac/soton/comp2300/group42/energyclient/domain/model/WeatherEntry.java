package uk.ac.soton.comp2300.group42.energyclient.domain.model;

import uk.ac.soton.comp2300.group42.energyclient.data.external.CurrentWeather;
import uk.ac.soton.comp2300.group42.energyclient.data.external.HourlyWeather;

public record WeatherEntry(
        double latitude,
        double longitude,
        CurrentWeather current,
        HourlyWeather hourly
) {
}
