package uk.ac.soton.comp2300.group42.energyclient.data.external;

public record CurrentWeather(
        String time,
        double temperature_2m,
        double shortwave_radiation
) {}
