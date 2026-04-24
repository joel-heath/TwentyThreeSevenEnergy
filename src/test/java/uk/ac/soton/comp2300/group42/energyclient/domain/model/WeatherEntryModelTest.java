package uk.ac.soton.comp2300.group42.energyclient.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeatherEntryModelTest {

    @Test
    void weatherModels_storeAndExposeValues() {
        CurrentWeather current = new CurrentWeather("2026-04-24T09:00", 14.3, 190.0);
        HourlyWeather hourly = new HourlyWeather(
                List.of("2026-04-24T09:00", "2026-04-24T10:00"),
                List.of(14.3, 15.2),
                List.of(190.0, 240.0)
        );
        WeatherEntry entry = new WeatherEntry(50.93, -1.39, current, hourly);

        assertEquals("2026-04-24T09:00", current.time());
        assertEquals(14.3, current.temperature_2m());
        assertEquals(190.0, current.shortwave_radiation());
        assertEquals(List.of("2026-04-24T09:00", "2026-04-24T10:00"), hourly.time());
        assertEquals(List.of(14.3, 15.2), hourly.temperature_2m());
        assertEquals(List.of(190.0, 240.0), hourly.shortwave_radiation());
        assertEquals(50.93, entry.latitude());
        assertEquals(-1.39, entry.longitude());
        assertEquals(current, entry.current());
        assertEquals(hourly, entry.hourly());
    }
}
