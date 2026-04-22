package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import uk.ac.soton.comp2300.group42.energyclient.data.external.CurrentWeather;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.WeatherEntry;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.WeatherRepository;

public class CurrentWeatherViewModel {

    private final WeatherRepository weatherRepo;

    private final StringProperty temperature = new SimpleStringProperty("Temperature: --°C");
    private final StringProperty sunlightIntensity = new SimpleStringProperty("Solar Intensity: -- W/m²");

    @Inject
    public CurrentWeatherViewModel(WeatherRepository weatherRepo) {
        this.weatherRepo = weatherRepo;
        refresh();
    }

    public void refresh() {
        WeatherEntry currentWeatherEntry = weatherRepo.fetchCurrentWeather();
        CurrentWeather current = currentWeatherEntry.current();
        try {
            temperature.set(String.format("Temperature: %.1f°C", current.temperature_2m()));
            sunlightIntensity.set(String.format("Solar Intensity: %.0f W/m²", current.shortwave_radiation()));
        } catch (NullPointerException e) {
            temperature.set("Temperature: --°C");
            sunlightIntensity.set("Solar Intensity: -- W/m²");
        }
    }

    public StringProperty temperatureProperty() { return temperature; }
    public StringProperty sunlightIntensityProperty() { return sunlightIntensity; }
}
