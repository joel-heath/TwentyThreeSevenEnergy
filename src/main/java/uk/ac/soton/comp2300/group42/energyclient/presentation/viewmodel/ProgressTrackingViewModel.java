package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.UnitRate;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.EnergyPriceRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

public class ProgressTrackingViewModel {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final EnergyPriceRepository repository;

    private final ObservableList<XYChart.Series<String, Number>> priceSeriesData;
    private final ObservableList<XYChart.Series<String, Number>> expenseSeriesData;

    private final DoubleProperty currentPrice;

    @Inject
    public ProgressTrackingViewModel(EnergyPriceRepository repository) {
        this.repository = repository;
        this.priceSeriesData = FXCollections.observableArrayList();
        this.expenseSeriesData = FXCollections.observableArrayList();
        this.currentPrice = new SimpleDoubleProperty(0.0);
    }

    public ObservableList<XYChart.Series<String, Number>> getPriceSeriesData() {
        return priceSeriesData;
    }

    public ObservableList<XYChart.Series<String, Number>> getExpenseSeriesData() {
        return expenseSeriesData;
    }

    public DoubleProperty currentPriceProperty() {
        return currentPrice;
    }

    public void loadData() {
        List<UnitRate> rates = repository.fetchNext12Hours();
        currentPrice.set(rates.getFirst().valueIncVat());

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Price Trend (p/kWh)");

        for (UnitRate rate : rates) {
            String timeLabel = rate.validFrom().format(TIME_FORMATTER);
            series.getData().add(new XYChart.Data<>(timeLabel, rate.valueIncVat()));
        }

        priceSeriesData.clear();
        priceSeriesData.add(series);
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
