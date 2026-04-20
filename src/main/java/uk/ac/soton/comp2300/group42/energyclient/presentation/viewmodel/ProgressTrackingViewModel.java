package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.ac.soton.comp2300.group42.common.EnergyCategory;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.UIExecutor;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Metric;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.UnitRate;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.EnergyPriceRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.MetricRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.ColorVisionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.InputFeedbackManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.IntStream;

public class ProgressTrackingViewModel {

    public record DataPoint(String label, Number value) {}

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM");

    private final EnergyPriceRepository energyPriceRepo;
    private final MetricRepository metricRepo;
    private final ObservablePreferences preferences;
    private final InputFeedbackManager inputFeedbackManager;
    private final Executor uiExecutor;

    private final ObservableList<DataPoint> priceData = FXCollections.observableArrayList();

    private final ObservableList<DataPoint> expenseData = FXCollections.observableArrayList();
    private final ObservableList<DataPoint> electricityExpenseData = FXCollections.observableArrayList();
    private final ObservableList<DataPoint> gasExpenseData = FXCollections.observableArrayList();
    private final ObservableList<DataPoint> otherExpenseData = FXCollections.observableArrayList();

    private final ObservableList<DataPoint> usageData = FXCollections.observableArrayList();
    private final ObservableList<DataPoint> electricityUsageData = FXCollections.observableArrayList();
    private final ObservableList<DataPoint> gasUsageData = FXCollections.observableArrayList();
    private final ObservableList<DataPoint> otherUsageData = FXCollections.observableArrayList();

    private final StringProperty logUsageInput = new SimpleStringProperty("");
    private final ObjectProperty<EnergyCategory> selectedCategory = new SimpleObjectProperty<>(EnergyCategory.OTHER);
    private final StringProperty priceLabelText = new SimpleStringProperty("Loading...");
    private final ObjectProperty<ColorVisionManager.ColorRole> priceLabelRole = new SimpleObjectProperty<>(ColorVisionManager.ColorRole.WIDGET_TEXT);

    @Inject
    public ProgressTrackingViewModel(EnergyPriceRepository energyPriceRepo,
                                     MetricRepository metricRepo,
                                     ObservablePreferences preferences,
                                     InputFeedbackManager inputFeedbackManager,
                                     @UIExecutor Executor uiExecutor) {
        this.energyPriceRepo = energyPriceRepo;
        this.metricRepo = metricRepo;
        this.preferences = preferences;
        this.inputFeedbackManager = inputFeedbackManager;
        this.uiExecutor = uiExecutor;
    }

    private List<UnitRate> fetchNext12Hours() {
        return energyPriceRepo.fetchNext12Hours();
    }

    private List<UnitRate> fetchNext24Hours() {
        return energyPriceRepo.fetchNext24Hours();
    }

    public void initializeData() {
        CompletableFuture.runAsync(this::loadPriceDataAsync);

        loadAllChartDataAsync();
    }


    public void loadPriceDataAsync() {
        CompletableFuture.supplyAsync(energyPriceRepo::fetchNext12Hours)
                .thenAcceptAsync(rates -> {
                    if (!rates.isEmpty()) {
                        List<DataPoint> points = rates.stream()
                                .map(rate -> new DataPoint(rate.validFrom().format(TIME_FORMATTER), rate.valueIncVat()))
                                .toList();

                        priceData.setAll(points);
                        priceLabelText.set(String.format("%.2f p/kWh", rates.getFirst().valueIncVat()));
                        priceLabelRole.set(ColorVisionManager.ColorRole.WIDGET_TEXT);
                    }
                }, uiExecutor)
                .exceptionallyAsync(e -> {
                    priceLabelText.set("Failed to load data.");
                    priceLabelRole.set(ColorVisionManager.ColorRole.VALIDATION_ERROR);
                    System.err.println("Error loading price data: " + e.getMessage());
                    return null;
                }, uiExecutor);
    }

    public void logUsage() {
        String rawInput = logUsageInput.get() == null ? "" : logUsageInput.get().trim();
        if (rawInput.isEmpty()) {
            inputFeedbackManager.showPopup("Invalid Input", "Please enter a value to log.");
            return;
        }

        try {
            double energyUsed = Double.parseDouble(rawInput);
            EnergyCategory category = selectedCategory.get();
            List<UnitRate> next12Hours = fetchNext12Hours();
            double energyPrice = next12Hours.getFirst().valueIncVat() * energyUsed;

            CompletableFuture.runAsync(() -> {
                        Metric metric = new Metric(null, preferences.getActiveHouse().getId(), LocalDateTime.now(), energyUsed, energyPrice, category);
                        metricRepo.add(metric, category);
                    }).thenRunAsync(() -> {
                                logUsageInput.set("");
                                inputFeedbackManager.showPopup("Success", "Logged " + energyUsed + " kWh.");
                            }, uiExecutor
                    ).thenRun(this::loadAllChartDataAsync)
                    .exceptionallyAsync(e -> {
                        inputFeedbackManager.showPopup("Error", "Failed to save usage data.");
                        System.err.println("Failed to log usage: " + e.getMessage());
                        return null;
                    }, uiExecutor);
        } catch (NumberFormatException e) {
            inputFeedbackManager.showPopup("Invalid Input", "Please enter a valid numeric value.");
        }
    }

    private void loadAllChartDataAsync() {
        loadWeeklyExpensesAsync();
        loadWeeklyExpensesByCategoryAsync(EnergyCategory.ELECTRICITY, electricityExpenseData);
        loadWeeklyExpensesByCategoryAsync(EnergyCategory.GAS, gasExpenseData);
        loadWeeklyExpensesByCategoryAsync(EnergyCategory.OTHER, otherExpenseData);

        loadWeeklyUsagesAsync();
        loadWeeklyUsagesByCategoryAsync(EnergyCategory.ELECTRICITY, electricityUsageData);
        loadWeeklyUsagesByCategoryAsync(EnergyCategory.GAS, gasUsageData);
        loadWeeklyUsagesByCategoryAsync(EnergyCategory.OTHER, otherUsageData);
    }

    private void loadWeeklyExpensesAsync() {
        CompletableFuture.supplyAsync(() -> {
            List<LocalDate> lastSevenDays = IntStream.range(0, 7).mapToObj(i -> LocalDate.now().minusDays(i)).sorted().toList();
            return lastSevenDays.stream().map(date -> {
                double total = metricRepo.getAllByDate(preferences.getActiveHouse().getId(), date)
                        .stream()
                        .mapToDouble(Metric::energyPrice).sum() / 100.0;
                return new DataPoint(date.format(DATE_FORMATTER), total);
            }).toList();
        }).thenAcceptAsync(expenseData::setAll, uiExecutor);
    }

    private void loadWeeklyExpensesByCategoryAsync(EnergyCategory category, ObservableList<DataPoint> targetList) {
        CompletableFuture.supplyAsync(() -> {
            List<LocalDate> lastSevenDays = IntStream.range(0, 7).mapToObj(i -> LocalDate.now().minusDays(i)).sorted().toList();
            return lastSevenDays.stream().map(date -> {
                double total = metricRepo.getAllByDate(preferences.getActiveHouse().getId(), date)
                        .stream()
                        .filter(cost -> cost.category() == category)
                        .mapToDouble(Metric::energyPrice).sum() / 100.0;
                return new DataPoint(date.format(DATE_FORMATTER), total);
            }).toList();
        }).thenAcceptAsync(targetList::setAll, uiExecutor);
    }

    private void loadWeeklyUsagesAsync() {
        CompletableFuture.supplyAsync(() -> {
            List<LocalDate> lastSevenDays = IntStream.range(0, 7).mapToObj(i -> LocalDate.now().minusDays(i)).sorted().toList();
            return lastSevenDays.stream().map(date -> {
                double total = metricRepo.getAllByDate(preferences.getActiveHouse().getId(), date)
                        .stream().mapToDouble(Metric::energyUsed).sum();
                return new DataPoint(date.format(DATE_FORMATTER), total);
            }).toList();
        }).thenAcceptAsync(usageData::setAll, uiExecutor);
    }

    private void loadWeeklyUsagesByCategoryAsync(EnergyCategory category, ObservableList<DataPoint> targetList) {
        CompletableFuture.supplyAsync(() -> {
            List<LocalDate> lastSevenDays = IntStream.range(0, 7).mapToObj(i -> LocalDate.now().minusDays(i)).sorted().toList();
            return lastSevenDays.stream().map(date -> {
                double total = metricRepo.getAllByDate(preferences.getActiveHouse().getId(), date)
                        .stream()
                        .filter(metric -> metric.category() == category)
                        .mapToDouble(Metric::energyUsed).sum();
                return new DataPoint(date.format(DATE_FORMATTER), total);
            }).toList();
        }).thenAcceptAsync(targetList::setAll, uiExecutor);
    }

    public ObservableList<DataPoint> getPriceData() { return priceData; }

    public ObservableList<DataPoint> getExpenseData() { return expenseData; }
    public ObservableList<DataPoint> getElectricityExpenseData() { return electricityExpenseData; }
    public ObservableList<DataPoint> getGasExpenseData() { return gasExpenseData; }
    public ObservableList<DataPoint> getOtherExpenseData() { return otherExpenseData; }

    public ObservableList<DataPoint> getUsageData() { return usageData; }
    public ObservableList<DataPoint> getElectricityUsageData() { return electricityUsageData; }
    public ObservableList<DataPoint> getGasUsageData() { return gasUsageData; }
    public ObservableList<DataPoint> getOtherUsageData() { return otherUsageData; }

    public StringProperty logUsageInputProperty() { return logUsageInput; }
    public ObjectProperty<EnergyCategory> selectedCategoryProperty() { return selectedCategory; }
    public StringProperty priceLabelTextProperty() { return priceLabelText; }
    public ObjectProperty<ColorVisionManager.ColorRole> priceLabelRoleProperty() { return priceLabelRole; }
}