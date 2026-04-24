package uk.ac.soton.comp2300.group42.energyclient.domain.model;

public record CurrentWeather(
        String time,
        double temperature_2m,
        double shortwave_radiation
) {
}
