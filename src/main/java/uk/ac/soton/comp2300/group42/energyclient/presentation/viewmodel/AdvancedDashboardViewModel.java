package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.UnitRate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.services.EnergyPriceService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdvancedDashboardViewModel {

    private final EnergyPriceService service = new EnergyPriceService();
    private final ObservableList<UnitRate> hourlyForecast = FXCollections.observableArrayList();

    public void loadDashboardData() {
        try {
            List<UnitRate> allRates = service.fetchNext12Hours();
            Collections.reverse(allRates);

            List<UnitRate> hourlyRates = new ArrayList<>();

            for (UnitRate rate : allRates) {
                if (rate.validFrom().endsWith(":00:00Z")) {
                    hourlyRates.add(rate);
                }
            }

            Platform.runLater(() -> {
                hourlyForecast.setAll(hourlyRates);
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    public ObservableList<UnitRate> getHourlyForecast() {
        return hourlyForecast;
    }
}
