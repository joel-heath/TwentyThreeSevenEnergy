package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.UnitRate;
import uk.ac.soton.comp2300.group42.energyclient.ui.services.EnergyPriceService;

import java.util.Collections;
import java.util.List;

public class ProgressTrackingViewModel {
    private final EnergyPriceService service = new EnergyPriceService();

    private final ObservableList<XYChart.Series<String, Number>> priceSeriesData = FXCollections.observableArrayList();
    private final ObservableList<XYChart.Series<String, Number>> expenseSeriesData = FXCollections.observableArrayList();

    public ObservableList<XYChart.Series<String, Number>> getPriceSeriesData() {
        return priceSeriesData;
    }

    private final DoubleProperty currentPrice = new SimpleDoubleProperty();

    public DoubleProperty currentPriceProperty() {
        return currentPrice;
    }

    public void loadData() {
        try {
            List<UnitRate> rates = service.fetchNext12Hours();

            if (!rates.isEmpty()) {
                currentPrice.set(rates.getFirst().valueIncVat());
            }

            Collections.reverse(rates);

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Price Trend (p/kWh)");

            for (UnitRate rate : rates) {
                // Formatting time from "2025-02-19T14:00:00Z" to "14:00"
                String timeLabel = rate.validFrom().substring(11, 16);
                series.getData().add(new XYChart.Data<>(timeLabel, rate.valueIncVat()));
            }

            priceSeriesData.clear();
            priceSeriesData.add(series);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
