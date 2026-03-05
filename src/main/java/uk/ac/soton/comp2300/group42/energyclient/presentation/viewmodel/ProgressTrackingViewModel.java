package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Metric;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.UnitRate;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.EnergyPriceRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.MetricRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProgressTrackingViewModel {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd");


    private final EnergyPriceRepository repository;
    private final ObservablePreferences preferences;

    private final ObservableList<XYChart.Series<String, Number>> priceSeriesData;
    private final ObservableList<XYChart.Series<String, Number>> expenseSeriesData;

    private final MetricRepository metricRepo;
    private final DoubleProperty currentPrice;


    @Inject
    public ProgressTrackingViewModel(EnergyPriceRepository repository, MetricRepository metricRepo, ObservablePreferences preferences) {
        this.repository = repository;
        this.priceSeriesData = FXCollections.observableArrayList();
        this.expenseSeriesData = FXCollections.observableArrayList();
        this.currentPrice = new SimpleDoubleProperty(0.0);
        this.metricRepo = metricRepo;
        this.preferences = preferences;
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

    public CompletableFuture<Void> loadDataAsync() {
        return CompletableFuture.runAsync(() -> {
            List<UnitRate> rates = repository.fetchNext12Hours();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Price Trend (p/kWh)");

            for (UnitRate rate : rates) {
                String timeLabel = rate.validFrom().format(TIME_FORMATTER);
                series.getData().add(new XYChart.Data<>(timeLabel, rate.valueIncVat()));
            }

            Platform.runLater(() -> {
                currentPrice.set(rates.getFirst().valueIncVat());
                priceSeriesData.clear();
                priceSeriesData.add(series);
            });
        });
    }

    public void loadMockExpenses() {
        expenseSeriesData.clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Daily Spend (Mock)");

        List<Metric> testList = this.metricRepo.getAll(preferences.getActiveHouse().getId());
        List<Double> energyValues = testList.stream()
                .map(Metric::energyUsed)
                .toList();
        List<LocalDate> dateValues = testList.stream()
                .map(Metric::date)
                .toList();

        int maxLen = energyValues.size();
        for (int i = 0; i < maxLen; i++) {
            String dateLabel = dateValues.get(i).format(DATE_FORMATTER);
            series.getData().add(new XYChart.Data<>(dateLabel, energyValues.get(i)));
        }

        expenseSeriesData.add(series);
    }
}
