package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import uk.ac.soton.comp2300.group42.common.EnergyCategory;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.EnergyCost;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Metric;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.UnitRate;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.EnergyPriceRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.MetricRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProgressTrackingViewModel {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd");

    private final ObservablePreferences preferences;

    private final ObservableList<XYChart.Series<String, Number>> priceSeriesData;
    private final ObservableList<XYChart.Series<String, Number>> expenseSeriesData;
    private final ObservableList<XYChart.Series<String, Number>> usageSeriesData;
    private final ObservableList<XYChart.Series<String, Number>> electricitySeriesData;
    private final ObservableList<XYChart.Series<String, Number>> gasSeriesData;
    private final ObservableList<XYChart.Series<String, Number>> otherExpenseSeriesData;

    private final MetricRepository metricRepo;
    private final EnergyPriceRepository energyPriceRepo;
    private final DoubleProperty currentPrice;
    private final ObjectProperty<EnergyCategory> selectedCategory;

    @Inject
    public ProgressTrackingViewModel(EnergyPriceRepository energyPriceRepo, MetricRepository metricRepo, ObservablePreferences preferences) {
        this.energyPriceRepo = energyPriceRepo;
        this.priceSeriesData = FXCollections.observableArrayList();
        this.expenseSeriesData = FXCollections.observableArrayList();
        this.usageSeriesData = FXCollections.observableArrayList();
        this.electricitySeriesData = FXCollections.observableArrayList();
        this.gasSeriesData = FXCollections.observableArrayList();
        this.otherExpenseSeriesData = FXCollections.observableArrayList();

        this.currentPrice = new SimpleDoubleProperty(0.0);
        this.metricRepo = metricRepo;
        this.preferences = preferences;
        this.selectedCategory = new SimpleObjectProperty<>(EnergyCategory.OTHER);
    }

    public ObservableList<XYChart.Series<String, Number>> getPriceSeriesData() {
        return priceSeriesData;
    }
    public ObservableList<XYChart.Series<String, Number>> getExpenseSeriesData() { return expenseSeriesData; }
    public ObservableList<XYChart.Series<String, Number>> getUsageSeriesData() { return usageSeriesData; }
    public ObservableList<XYChart.Series<String, Number>> getElectricitySeriesData() { return electricitySeriesData; }
    public ObservableList<XYChart.Series<String, Number>> getGasSeriesData() { return gasSeriesData; }
    public ObservableList<XYChart.Series<String, Number>> getOtherExpenseSeriesData() { return otherExpenseSeriesData; }

    public DoubleProperty currentPriceProperty() { return currentPrice; }
    public ObjectProperty<EnergyCategory> selectedCategoryProperty() { return selectedCategory; }

    public void syncPrices() {
        energyPriceRepo.syncAndGetNext24Hours();
    }

    public CompletableFuture<Void> loadDataAsync() {
        return CompletableFuture.runAsync(() -> {
            List<UnitRate> rates = energyPriceRepo.fetchNext12Hours();

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

    public List<Metric> getAllMetricsByDate(LocalDate date) {
        return this.metricRepo.getAllByDate(preferences.getActiveHouse().getId(), date);
    }

    public List<Metric> getAllMetricsByCategory(EnergyCategory category) {
        return this.metricRepo.getAllByCategory(preferences.getActiveHouse().getId(), category);
    }

    public List<EnergyCost> getCostsForDate(LocalDate date) {
        return energyPriceRepo.getCostsForDate(preferences.getActiveHouse().getId(), date);
    }

    public void loadWeeklyExpenses() {
        expenseSeriesData.clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Last 7 Days Spend (£)");

        List<LocalDate> lastSevenDays = IntStream.range(0, 7)
                .mapToObj(i -> LocalDate.now().minusDays(i))
                .sorted()
                .toList();

        for (LocalDate date : lastSevenDays) {
            double dayTotalSpend = getCostsForDate(date)
                    .stream()
                    .mapToDouble(EnergyCost::totalCost)
                    .sum() / 100; // convert pence to pounds

            String label = date.format(DateTimeFormatter.ofPattern("dd/MM"));
            series.getData().add(new XYChart.Data<>(label, dayTotalSpend));
        }

        expenseSeriesData.add(series);
    }

    public void loadMockExpenses() {
        usageSeriesData.clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Daily Total Spend");

        List<Metric> metricList = getAllMetrics();

        Map<LocalDate, Double> dailyTotals = metricList.stream()
                .filter(metric -> metric.dateTime() != null)
                .collect(Collectors.groupingBy(
                        metric -> metric.dateTime().toLocalDate(),
                        TreeMap::new,
                        Collectors.summingDouble(Metric::energyUsed)
                ));

        dailyTotals.forEach((date, total) -> {
            String dateLabel = date.format(DATE_FORMATTER);
            series.getData().add(new XYChart.Data<>(dateLabel, total));
        });

        usageSeriesData.add(series);
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

        Map<LocalDate, Double> dailyTotals = metricList.stream()
                .collect(Collectors.groupingBy(
                        metric -> metric.dateTime().toLocalDate(),
                        TreeMap::new,
                        Collectors.summingDouble(Metric::energyUsed)
                ));

        dailyTotals.forEach((date, total) -> {
            String dateLabel = date.format(DATE_FORMATTER);
            series.getData().add(new XYChart.Data<>(dateLabel, total));
        });

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
        System.out.println("Logging usage: " + energyUsed + " kWh for category " + selectedCategory.get());
        Metric metric = new Metric(null, preferences.getActiveHouse().getId(), LocalDateTime.now(), energyUsed, selectedCategory.get());
        metricRepo.add(metric, selectedCategory.get());
    }
}
