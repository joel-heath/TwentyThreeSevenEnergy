package uk.ac.soton.comp2300.group42.energyclient.data.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.ac.soton.comp2300.group42.energyclient.data.external.CurrentWeather;
import uk.ac.soton.comp2300.group42.energyclient.data.external.HourlyWeather;
import uk.ac.soton.comp2300.group42.energyclient.data.external.WeatherResponse;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.WeatherEntry;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class WeatherEntryMapperTest {

    private final WeatherEntryMapper mapper = Mappers.getMapper(WeatherEntryMapper.class);

    @Test
    void toResponse_mapsNestedWeatherResponse() {
        CurrentWeather current = new CurrentWeather("2026-04-24T10:00", 17.5, 320.0);
        HourlyWeather hourly = new HourlyWeather(
                List.of("2026-04-24T10:00", "2026-04-24T11:00"),
                List.of(17.5, 18.1),
                List.of(320.0, 360.5)
        );
        WeatherResponse response = new WeatherResponse(50.93, -1.39, current, hourly);

        WeatherEntry mapped = mapper.toResponse(response);

        assertNotNull(mapped);
        assertEquals(50.93, mapped.latitude());
        assertEquals(-1.39, mapped.longitude());
        assertNotNull(mapped.current());
        assertNotNull(mapped.hourly());
        assertEquals("2026-04-24T10:00", mapped.current().time());
        assertEquals(17.5, mapped.current().temperature_2m());
        assertEquals(320.0, mapped.current().shortwave_radiation());
        assertEquals(List.of("2026-04-24T10:00", "2026-04-24T11:00"), mapped.hourly().time());
        assertEquals(List.of(17.5, 18.1), mapped.hourly().temperature_2m());
        assertEquals(List.of(320.0, 360.5), mapped.hourly().shortwave_radiation());
    }

    @Test
    void toResponse_nullInputs_returnNull() {
        assertNull(mapper.toResponse((WeatherResponse) null));
        assertNull(mapper.toResponse((CurrentWeather) null));
        assertNull(mapper.toResponse((HourlyWeather) null));
    }
}
