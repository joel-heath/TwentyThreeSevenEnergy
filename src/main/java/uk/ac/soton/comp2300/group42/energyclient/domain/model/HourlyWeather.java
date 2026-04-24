package uk.ac.soton.comp2300.group42.energyclient.domain.model;

import java.util.List;

public record HourlyWeather(
        List<String> time,
        List<Double> temperature_2m,
        List<Double> shortwave_radiation
) {
}
