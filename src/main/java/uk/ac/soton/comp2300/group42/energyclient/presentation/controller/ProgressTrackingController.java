package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.common.EnergyCategory;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.ColorVisionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.ProgressTrackingViewModel;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static uk.ac.soton.comp2300.group42.energyclient.presentation.util.ControllerUtils.createConverter;

public class ProgressTrackingController {

    @FXML private LineChart<String, Number> priceChart;

    @FXML private VBox expensesViewContainer;
    @FXML private BarChart<String, Number> expensesChart, electricityExpensesChart, gasExpensesChart, otherExpensesChart;

    @FXML private VBox usageViewContainer;
    @FXML private BarChart<String, Number> usageChart, electricityUsageChart, gasUsageChart, otherUsageChart;

    @FXML private ToggleGroup expenseUsageToggleGroup;
    @FXML private ToggleGroup navToggleGroup;
    @FXML private ToggleButton btnExpenses, btnElec, btnGas, btnOther;

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
        vm.priceLabelRoleProperty().subscribe(newVal ->
                priceLabel.setTextFill(ColorVisionManager.getColor(newVal))
        );

        logUsageField.textProperty().bindBidirectional(vm.logUsageInputProperty());

        categoryComboBox.getItems().setAll(EnergyCategory.values());
        categoryComboBox.setConverter(createConverter(EnergyCategory::getName));
        categoryComboBox.valueProperty().bindBidirectional(vm.selectedCategoryProperty());

        bindChartData(priceChart, "Price Trend (p/kWh)", vm.getPriceData());
        bindChartData(expensesChart, "Last 7 Days Spend (£)", vm.getExpenseData());
        bindChartData(usageChart, "Daily Total Spend", vm.getUsageData());
        bindChartData(electricityUsageChart, "Daily Spend (Electricity)", vm.getElectricityData());
        bindChartData(gasUsageChart, "Daily Spend (Gas)", vm.getGasData());
        bindChartData(otherUsageChart, "Daily Spend (Other)", vm.getOtherExpenseData());

        expenseUsageToggleGroup.selectedToggleProperty().subscribe(this::updateVisibility);
        navToggleGroup.selectedToggleProperty().subscribe(this::updateVisibility);
        updateVisibility();

        ColorVisionManager.visionProperty().subscribe(this::scheduleApplyChartColours);

        vm.initializeData();
    }

    @FXML private void onLogUsage() {
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
                scheduleApplyChartColours();
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
                electricityUsageChart, gasUsageChart, otherUsageChart
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
        } else if (selectedNav == btnOther) {
            toShow = isExpenseMode ? otherExpensesChart : otherUsageChart;
        }

        if (toShow != null) {
            toShow.setVisible(true);
            toShow.setManaged(true);
        }
    }

    private void scheduleApplyChartColours() {
        Platform.runLater(this::applyChartColours);
    }

    private void applyChartColours() {
        priceChart.applyCss();
        priceChart.layout();
        expensesChart.applyCss();
        expensesChart.layout();
        usageChart.applyCss();
        usageChart.layout();
        electricityUsageChart.applyCss();
        electricityUsageChart.layout();
        gasUsageChart.applyCss();
        gasUsageChart.layout();
        otherUsageChart.applyCss();
        otherUsageChart.layout();

        String lineColour = ColorVisionManager.getWebColor(ColorVisionManager.ColorRole.STATUS_EXPENSIVE);
        String barColour = ColorVisionManager.getWebColor(ColorVisionManager.ColorRole.STATUS_AVERAGE);

        for (XYChart.Series<String, Number> series : priceChart.getData()) {
            Node seriesNode = series.getNode();
            if (seriesNode != null) {
                Node lineNode = seriesNode.lookup(".chart-series-line");
                if (lineNode != null) {
                    lineNode.setStyle("-fx-stroke: " + lineColour + ";");
                }
            }

            for (XYChart.Data<String, Number> data : series.getData()) {
                Node symbolNode = data.getNode();
                if (symbolNode != null) {
                    symbolNode.setStyle("-fx-background-color: " + lineColour + ", white;");
                }
            }
        }

        styleChart(barColour, expensesChart);
        styleChart(barColour, usageChart);
        styleChart(lineColour, electricityUsageChart);
        styleChart(lineColour, gasUsageChart);
        styleChart(lineColour, otherUsageChart);

        styleLegend(lineColour, barColour, priceChart.lookupAll(".chart-legend-item-symbol"), expensesChart, usageChart);
        styleLegend(barColour, barColour, electricityUsageChart.lookupAll(".chart-legend-item-symbol"), gasUsageChart, otherUsageChart);
    }

    private void styleLegend(String lineColour, String barColour, Set<Node> nodes, BarChart<String, Number> expensesChart, BarChart<String, Number> usageChart) {
        for (Node legendSymbol : nodes) {
            legendSymbol.setStyle("-fx-background-color: " + lineColour + ", " + lineColour + ";");
        }
        for (Node legendSymbol : expensesChart.lookupAll(".chart-legend-item-symbol")) {
            legendSymbol.setStyle("-fx-background-color: " + barColour + ", " + barColour + ";");
        }
        for (Node legendSymbol : usageChart.lookupAll(".chart-legend-item-symbol")) {
            legendSymbol.setStyle("-fx-background-color: " + barColour + ", " + barColour + ";");
        }
    }

    private void styleChart(String lineColour, BarChart<String, Number> otherUsageChart) {
        for (XYChart.Series<String, Number> series : otherUsageChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node barNode = data.getNode();
                if (barNode != null) {
                    barNode.setStyle("-fx-bar-fill: " + lineColour + ";");
                }
            }
        }
    }
}
