package uk.ac.soton.comp2300.group42.energyclient.data.mapper;

import org.mapstruct.Mapper;
import uk.ac.soton.comp2300.group42.energyclient.data.external.CurrentWeather;
import uk.ac.soton.comp2300.group42.energyclient.data.external.HourlyWeather;
import uk.ac.soton.comp2300.group42.energyclient.data.external.WeatherResponse;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.WeatherEntry;

@Mapper
public interface WeatherEntryMapper {
    WeatherEntry toResponse(WeatherResponse response);

    uk.ac.soton.comp2300.group42.energyclient.domain.model.CurrentWeather toResponse(CurrentWeather currentWeather);

    uk.ac.soton.comp2300.group42.energyclient.domain.model.HourlyWeather toResponse(HourlyWeather hourlyWeather);
}
