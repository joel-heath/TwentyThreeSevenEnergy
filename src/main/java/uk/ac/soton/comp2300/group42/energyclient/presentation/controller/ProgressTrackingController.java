package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.common.EnergyCategory;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.StyleClassUtils;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.ProgressTrackingViewModel;

import java.util.Arrays;
import java.util.List;

import static uk.ac.soton.comp2300.group42.energyclient.presentation.util.ControllerUtils.createConverter;

public class ProgressTrackingController {

    @FXML private LineChart<String, Number> priceChart;

    @FXML private VBox expensesViewContainer;
    @FXML private BarChart<String, Number> expensesChart, electricityExpensesChart, gasExpensesChart, otherExpensesChart;

    @FXML private VBox usageViewContainer;
    @FXML private BarChart<String, Number> usageChart, electricityUsageChart, gasUsageChart, solarUsageChart, otherUsageChart;

    @FXML private ToggleGroup expenseUsageToggleGroup;
    @FXML private ToggleGroup navToggleGroup;
    @FXML private ToggleButton btnExpenses, btnElec, btnGas, btnSolar, btnOther;

    @FXML private Label priceLabel;
    @FXML private TextField logUsageField;
    @FXML private ComboBox<EnergyCategory> categoryComboBox;

    private final ProgressTrackingViewModel vm;

    @Inject
    public ProgressTrackingController(ProgressTrackingViewModel vm) {
        this.vm = vm;
    }

    @FXML
    private void initialize() {
        priceLabel.textProperty().bind(vm.priceLabelTextProperty());
        StyleClassUtils.bindExclusiveClass(priceLabel, vm.priceLabelStyleClassProperty(), "response-error");

        logUsageField.textProperty().bindBidirectional(vm.logUsageInputProperty());

        categoryComboBox.getItems().setAll(EnergyCategory.values());
        categoryComboBox.setConverter(createConverter(EnergyCategory::getName));
        categoryComboBox.valueProperty().bindBidirectional(vm.selectedCategoryProperty());

        priceChart.getStyleClass().add("forecast-chart");
        expensesChart.getStyleClass().add("expense-chart");
        electricityExpensesChart.getStyleClass().add("expense-chart");
        gasExpensesChart.getStyleClass().add("expense-chart");
        otherExpensesChart.getStyleClass().add("expense-chart");
        usageChart.getStyleClass().add("expense-chart");
        electricityUsageChart.getStyleClass().add("expense-chart");
        gasUsageChart.getStyleClass().add("expense-chart");
        solarUsageChart.getStyleClass().add("expense-chart");
        otherUsageChart.getStyleClass().add("expense-chart");

        bindChartData(priceChart, "Price Trend (p/kWh)", vm.getPriceData());

        bindChartData(expensesChart, "Last 7 Days Spend (£)", vm.getExpenseData());
        bindChartData(electricityExpensesChart, "Last 7 Days Spend (Electricity)", vm.getElectricityExpenseData());
        bindChartData(gasExpensesChart, "Last 7 Days Spend (Gas)", vm.getGasExpenseData());
        bindChartData(otherExpensesChart, "Last 7 Days Spend (Other)", vm.getOtherExpenseData());

        bindChartData(usageChart, "Last 7 Days Usage (kWh)", vm.getUsageData());
        bindChartData(electricityUsageChart, "Last 7 Days Usage (Electricity)", vm.getElectricityUsageData());
        bindChartData(gasUsageChart, "Last 7 Days Usage (Gas)", vm.getGasUsageData());
        bindChartData(solarUsageChart, "Last 7 Days Usage (Solar)", vm.getSolarUsageData());
        bindChartData(otherUsageChart, "Last 7 Days Usage (Other)", vm.getOtherUsageData());

        expenseUsageToggleGroup.selectedToggleProperty().subscribe(this::updateVisibility);
        navToggleGroup.selectedToggleProperty().subscribe(this::updateVisibility);
        updateVisibility();

        vm.initializeData();
    }

    @FXML
    private void onLogUsage() {
        vm.logUsage();
    }

    private void bindChartData(XYChart<String, Number> chart, String seriesName, ObservableList<ProgressTrackingViewModel.DataPoint> dataPoints) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(seriesName);
        chart.getData().add(series);

        dataPoints.subscribe(() ->
                Platform.runLater(() -> {
                    List<XYChart.Data<String, Number>> fxData = dataPoints.stream()
                            .map(dp -> new XYChart.Data<>(dp.label(), dp.value()))
                            .toList();
                    series.getData().setAll(fxData);
                })
        );
    }

    private void updateVisibility() {
        boolean isExpenseMode = expenseUsageToggleGroup.getSelectedToggle() == btnExpenses;

        expensesViewContainer.setVisible(isExpenseMode);
        expensesViewContainer.setManaged(isExpenseMode);
        usageViewContainer.setVisible(!isExpenseMode);
        usageViewContainer.setManaged(!isExpenseMode);

        List<BarChart<String, Number>> categoryCharts = Arrays.asList(
                electricityExpensesChart, gasExpensesChart, otherExpensesChart,
                electricityUsageChart, gasUsageChart, solarUsageChart, otherUsageChart
        );
        for (BarChart<String, Number> chart : categoryCharts) {
            chart.setVisible(false);
            chart.setManaged(false);
        }

        ToggleButton selectedNav = (ToggleButton) navToggleGroup.getSelectedToggle();
        BarChart<String, Number> toShow = null;

        if (selectedNav == btnElec) {
            toShow = isExpenseMode ? electricityExpensesChart : electricityUsageChart;
        } else if (selectedNav == btnGas) {
            toShow = isExpenseMode ? gasExpensesChart : gasUsageChart;
        } else if (selectedNav == btnSolar) {
            toShow = isExpenseMode ? null : solarUsageChart; // No solar expenses chart
        } else if (selectedNav == btnOther) {
            toShow = isExpenseMode ? otherExpensesChart : otherUsageChart;
        }

        if (toShow != null) {
            toShow.setVisible(true);
            toShow.setManaged(true);
        }
    }
}
