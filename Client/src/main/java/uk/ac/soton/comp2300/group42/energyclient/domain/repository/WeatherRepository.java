package uk.ac.soton.comp2300.group42.energyclient.domain.repository;

import uk.ac.soton.comp2300.group42.energyclient.domain.model.WeatherEntry;

import java.util.List;

public interface WeatherRepository {
    WeatherEntry fetchCurrentWeather();
    List<WeatherEntry> fetchWeatherForecast();
}
