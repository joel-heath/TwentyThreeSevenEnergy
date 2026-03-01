package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.UnitRate;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.EnergyPriceRepository;

import java.util.List;

public class AdvancedDashboardViewModel {

    private final EnergyPriceRepository repository;
    private final ObservableList<UnitRate> hourlyForecast;

    @Inject
    public AdvancedDashboardViewModel(EnergyPriceRepository repository) {
        this.repository = repository;
        this.hourlyForecast = FXCollections.observableArrayList();
    }

    public void loadDashboardData() {
        List<UnitRate> hourlyRates = repository.fetchNext12Hours()
                .stream()
                .filter(rate -> rate.validFrom().getMinute() == 0)
                .toList();

        Platform.runLater(() -> hourlyForecast.setAll(hourlyRates));
    }

    public ObservableList<UnitRate> getHourlyForecast() {
        return hourlyForecast;
    }
}
