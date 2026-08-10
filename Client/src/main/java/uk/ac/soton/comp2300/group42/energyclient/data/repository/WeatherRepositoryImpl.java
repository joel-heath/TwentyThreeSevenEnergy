package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import uk.ac.soton.comp2300.group42.energyclient.data.external.LocationClient;
import uk.ac.soton.comp2300.group42.energyclient.data.external.LocationResponse;
import uk.ac.soton.comp2300.group42.energyclient.data.external.WeatherClient;
import uk.ac.soton.comp2300.group42.energyclient.data.external.WeatherResponse;
import uk.ac.soton.comp2300.group42.energyclient.data.mapper.WeatherEntryMapper;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.WeatherEntry;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.WeatherRepository;

import java.util.List;

public class WeatherRepositoryImpl implements WeatherRepository {

    private final LocationClient locationClient;
    private final WeatherClient externalClient;
    private final WeatherEntryMapper weatherEntryMapper;

    @Inject
    public WeatherRepositoryImpl(LocationClient locationClient, WeatherClient externalClient, WeatherEntryMapper weatherEntryMapper) {
        this.locationClient = locationClient;
        this.externalClient = externalClient;
        this.weatherEntryMapper = weatherEntryMapper;
    }

    @Override
    public WeatherEntry fetchCurrentWeather() {
        LocationResponse location = locationClient.fetchCurrentLocation();
        WeatherResponse response = externalClient.fetchCurrentWeather(location.lat(), location.lon());
        return weatherEntryMapper.toResponse(response);
    }

    @Override
    public List<WeatherEntry> fetchWeatherForecast() {
        LocationResponse location = locationClient.fetchCurrentLocation();
        return externalClient.fetchWeatherForecast(location.lat(), location.lon())
                .stream()
                .map(weatherEntryMapper::toResponse)
                .toList();
    }
}
