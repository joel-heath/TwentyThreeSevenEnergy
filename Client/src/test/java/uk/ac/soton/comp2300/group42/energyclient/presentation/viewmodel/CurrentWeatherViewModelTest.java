package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import javafx.beans.property.SimpleBooleanProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.CurrentWeather;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.WeatherEntry;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.WeatherRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrentWeatherViewModelTest {

    @Mock private WeatherRepository weatherRepository;
    @Mock private ObservablePreferences preferences;

    private CurrentWeatherViewModel viewModel;

    @BeforeEach
    void setUp() {
        viewModel = new CurrentWeatherViewModel(weatherRepository, preferences, Runnable::run);
    }

    @Test
    @DisplayName("refresh() should update properties with correctly formatted strings")
    void refresh_withValidData_updatesProperties() {
        CurrentWeather current = new CurrentWeather("2026-04-23T12:00", 21.56, 450.2);

        WeatherEntry entry = new WeatherEntry(50.0, -1.0, current, null);

        when(weatherRepository.fetchCurrentWeather()).thenReturn(entry);

        viewModel.refresh().join();

        assertEquals("Temperature: 21.6°C", viewModel.temperatureProperty().get());
        assertEquals("Solar Intensity: 450 W/m²", viewModel.sunlightIntensityProperty().get());
    }

    @Test
    @DisplayName("refresh() should reset to placeholders if current weather is null")
    void refresh_withNullCurrent_setsPlaceholders() {
        WeatherEntry entry = new WeatherEntry(50.0, -1.0, null, null);
        when(weatherRepository.fetchCurrentWeather()).thenReturn(entry);

        viewModel.refresh().join();

        assertEquals("Temperature: --°C", viewModel.temperatureProperty().get());
        assertEquals("Solar Intensity: -- W/m²", viewModel.sunlightIntensityProperty().get());
    }

    @Test
    @DisplayName("shareLocationProperty should delegate to preferences")
    void shareLocationProperty_delegatesToPreferences() {
        SimpleBooleanProperty expected = new SimpleBooleanProperty(true);
        when(preferences.shareLocationProperty()).thenReturn(expected);

        assertEquals(expected, viewModel.shareLocationProperty());
        verify(preferences).shareLocationProperty();
    }
}
