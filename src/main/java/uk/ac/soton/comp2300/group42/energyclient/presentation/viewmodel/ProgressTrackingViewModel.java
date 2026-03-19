package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import uk.ac.soton.comp2300.group42.common.EnergyCategory;
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
    private final ObservableList<XYChart.Series<String, Number>> electricitySeriesData;
    private final ObservableList<XYChart.Series<String, Number>> gasSeriesData;
    private final ObservableList<XYChart.Series<String, Number>> otherExpenseSeriesData;

    private final MetricRepository metricRepo;
    private final DoubleProperty currentPrice;


    @Inject
    public ProgressTrackingViewModel(EnergyPriceRepository repository, MetricRepository metricRepo, ObservablePreferences preferences) {
        this.repository = repository;
        this.priceSeriesData = FXCollections.observableArrayList();
        this.expenseSeriesData = FXCollections.observableArrayList();
        this.electricitySeriesData = FXCollections.observableArrayList();
        this.gasSeriesData = FXCollections.observableArrayList();
        this.otherExpenseSeriesData = FXCollections.observableArrayList();

        this.currentPrice = new SimpleDoubleProperty(0.0);
        this.metricRepo = metricRepo;
        this.preferences = preferences;
    }

    public ObservableList<XYChart.Series<String, Number>> getPriceSeriesData() {
        return priceSeriesData;
    }
    public ObservableList<XYChart.Series<String, Number>> getExpenseSeriesData() { return expenseSeriesData; }
    public ObservableList<XYChart.Series<String, Number>> getElectricitySeriesData() { return electricitySeriesData; }
    public ObservableList<XYChart.Series<String, Number>> getGasSeriesData() { return gasSeriesData; }
    public ObservableList<XYChart.Series<String, Number>> getOtherExpenseSeriesData() { return otherExpenseSeriesData; }

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

    public List<Metric> getAllMetrics() {
        return this.metricRepo.getAll(preferences.getActiveHouse().getId());
    }

    public List<Metric> getAllMetricsByCategory(EnergyCategory category) {
        return this.metricRepo.getAllByCategory(preferences.getActiveHouse().getId(), category);
    }

    public void loadMockExpenses() {
        expenseSeriesData.clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Daily Spend (Mock)");

        List<Metric> metricList = getAllMetrics();
        List<Double> energyValues = metricList.stream()
                .map(Metric::energyUsed)
                .toList();
        List<LocalDate> dateValues = metricList.stream()
                .map(Metric::date)
                .toList();

        int maxLen = energyValues.size();
        for (int i = 0; i < maxLen; i++) {
            String dateLabel = dateValues.get(i).format(DATE_FORMATTER);
            series.getData().add(new XYChart.Data<>(dateLabel, energyValues.get(i)));
        }

        expenseSeriesData.add(series);
    }

    public void loadExpensesByCategory(EnergyCategory category) {
        if (category == EnergyCategory.ELECTRICITY) {
            electricitySeriesData.clear();
        }
        else if (category == EnergyCategory.GAS) {
            gasSeriesData.clear();
        }
        else if (category == EnergyCategory.OTHER) {
            otherExpenseSeriesData.clear();
        }
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Daily Spend by Category (Mock)");

        List<Metric> metricList = getAllMetricsByCategory(category);
        List<Double> energyValues = metricList.stream()
                .map(Metric::energyUsed)
                .toList();
        List<LocalDate> dateValues = metricList.stream()
                .map(Metric::date)
                .toList();

        int maxLen = energyValues.size();
        for (int i = 0; i < maxLen; i++) {
            String dateLabel = dateValues.get(i).format(DATE_FORMATTER);
            series.getData().add(new XYChart.Data<>(dateLabel, energyValues.get(i)));
        }

        if (category == EnergyCategory.ELECTRICITY) {
            electricitySeriesData.add(series);
        }
        else if (category == EnergyCategory.GAS) {
            gasSeriesData.add(series);
        }
        else if (category == EnergyCategory.OTHER) {
            otherExpenseSeriesData.add(series);
        }
    }

    public void logUsage(double energyUsed) {
        Metric metric = new Metric(null, preferences.getActiveHouse().getId(), LocalDate.now(), energyUsed, EnergyCategory.OTHER);
        metricRepo.add(metric);
    }
}
