package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.UIExecutor;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.WeatherRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class CurrentWeatherViewModel {

    private final WeatherRepository weatherRepo;

    private final StringProperty temperature = new SimpleStringProperty("Temperature: --°C");
    private final StringProperty sunlightIntensity = new SimpleStringProperty("Solar Intensity: -- W/m²");
    private final ObservablePreferences preferences;
    private final Executor uiExecutor;

    @Inject
    public CurrentWeatherViewModel(WeatherRepository weatherRepo, ObservablePreferences pref, @UIExecutor Executor uiExecutor) {
        this.weatherRepo = weatherRepo;
        this.preferences = pref;
        this.uiExecutor = uiExecutor;
    }

    public CompletableFuture<Void> refresh() {
        return CompletableFuture.supplyAsync(() -> weatherRepo.fetchCurrentWeather().current())
                .thenAcceptAsync(weather -> {
                    if (weather != null) {
                        temperature.set(String.format("Temperature: %.1f°C", weather.temperature_2m()));
                        sunlightIntensity.set(String.format("Solar Intensity: %.0f W/m²", weather.shortwave_radiation()));
                    } else {
                        temperature.set("Temperature: --°C");
                        sunlightIntensity.set("Solar Intensity: -- W/m²");
                    }
                }, uiExecutor);
    }

    public StringProperty temperatureProperty() { return temperature; }
    public StringProperty sunlightIntensityProperty() { return sunlightIntensity; }
    public BooleanProperty shareLocationProperty() { return preferences.shareLocationProperty(); }
}
