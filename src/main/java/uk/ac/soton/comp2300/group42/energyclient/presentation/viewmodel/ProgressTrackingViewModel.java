package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.UnitRate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.services.EnergyPriceService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ProgressTrackingViewModel {

    private final EnergyPriceService service = new EnergyPriceService();

    private final ObservableList<XYChart.Series<String, Number>> priceSeriesData = FXCollections.observableArrayList();
    private final ObservableList<XYChart.Series<String, Number>> expenseSeriesData = FXCollections.observableArrayList();

    public ObservableList<XYChart.Series<String, Number>> getPriceSeriesData() {
        return priceSeriesData;
    }
    public ObservableList<XYChart.Series<String, Number>> getExpenseSeriesData() {
        return expenseSeriesData;
    }

    private final DoubleProperty currentPrice = new SimpleDoubleProperty();

    public DoubleProperty currentPriceProperty() {
        return currentPrice;
    }

    public void loadData() {
        try {
            List<UnitRate> rates = service.fetchNext12Hours();

            if (!rates.isEmpty()) {
                currentPrice.set(rates.getLast().valueIncVat());
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

    public void loadMockExpenses() {
        expenseSeriesData.clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Daily Spend (Mock)");

        Random random = new Random();
        LocalDate today = LocalDate.now();

        // Generate random energy costs between £1.50 and £5.00 for seven days
        for (int i = 6; i >= 0; i--) {
            String dateLabel = today.minusDays(i).getDayOfWeek().toString().substring(0, 3);
            double randomCost = 1.5 + (5.0 - 1.5) * random.nextDouble();

            series.getData().add(new XYChart.Data<>(dateLabel, randomCost));
        }

        expenseSeriesData.add(series);
    }
}
