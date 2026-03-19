package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import uk.ac.soton.comp2300.group42.common.EnergyCategory;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.ColorVisionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.ProgressTrackingViewModel;

public class ProgressTrackingController {

    @FXML private LineChart<String, Number> priceChart;
    @FXML private BarChart<String, Number> expenseChart;
    @FXML private BarChart<String, Number> electricityExpenseChart;
    @FXML private BarChart<String, Number> gasExpenseChart;
    @FXML private BarChart<String, Number> otherExpenseChart;

    @FXML private ToggleButton btnElec;
    @FXML private ToggleButton btnGas;
    @FXML private ToggleButton btnOther;

    @FXML private Label priceLabel;
    @FXML private TextField logUsageField;

    private final ProgressTrackingViewModel vm;
    @Inject public ProgressTrackingController(ProgressTrackingViewModel vm) { this.vm = vm; }
    private final BooleanProperty loadError = new SimpleBooleanProperty(false);

    @FXML private void initialize() {
        priceChart.setData(vm.getPriceSeriesData());
        expenseChart.setData(vm.getExpenseSeriesData());
        electricityExpenseChart.setData(vm.getElectricitySeriesData());
        gasExpenseChart.setData(vm.getGasSeriesData());
        otherExpenseChart.setData(vm.getOtherExpenseSeriesData());

        electricityExpenseChart.visibleProperty().bind(btnElec.selectedProperty());
        gasExpenseChart.visibleProperty().bind(btnGas.selectedProperty());
        otherExpenseChart.visibleProperty().bind(btnOther.selectedProperty());

        priceLabel.textProperty().bind(
            vm.currentPriceProperty().asString("%.2f p/kWh")
        );
        priceLabel.textFillProperty().bind(Bindings.createObjectBinding(
                () -> ColorVisionManager.getColor(
                        loadError.get()
                                ? ColorVisionManager.ColorRole.VALIDATION_ERROR
                                : ColorVisionManager.ColorRole.WIDGET_TEXT
                ),
                loadError,
                ColorVisionManager.visionProperty()
        ));

        vm.getPriceSeriesData().addListener((ListChangeListener<XYChart.Series<String, Number>>) _ -> scheduleApplyChartColours());
        vm.getExpenseSeriesData().addListener((ListChangeListener<XYChart.Series<String, Number>>) _ -> scheduleApplyChartColours());
        vm.getElectricitySeriesData().addListener((ListChangeListener<XYChart.Series<String, Number>>) _ -> scheduleApplyChartColours());
        ColorVisionManager.visionProperty().addListener((_, _, _) -> scheduleApplyChartColours());
        scheduleApplyChartColours();

        vm.loadMockExpenses(); // when real data is available, do this asynchronously
        vm.loadExpensesByCategory(EnergyCategory.ELECTRICITY);
        vm.loadExpensesByCategory(EnergyCategory.GAS);
        vm.loadExpensesByCategory(EnergyCategory.OTHER);
        loadError.set(false);

        vm.loadDataAsync().exceptionally(e -> {
            Platform.runLater(() -> {
                loadError.set(true);
                priceLabel.textProperty().unbind();
                priceLabel.setText("Failed to load data.");
            });
            System.out.println("Error loading price data: " + e.getMessage());
            return null;
        });
    }

    private void scheduleApplyChartColours() {
        Platform.runLater(this::applyChartColours);
    }

    private void applyChartColours() {
        priceChart.applyCss();
        priceChart.layout();
        expenseChart.applyCss();
        expenseChart.layout();
        electricityExpenseChart.applyCss();
        electricityExpenseChart.layout();
        gasExpenseChart.applyCss();
        gasExpenseChart.layout();
        otherExpenseChart.applyCss();
        otherExpenseChart.layout();

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

        for (XYChart.Series<String, Number> series : expenseChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node barNode = data.getNode();
                if (barNode != null) {
                    barNode.setStyle("-fx-bar-fill: " + barColour + ";");
                }
            }
        }

        for (XYChart.Series<String, Number> series : electricityExpenseChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node barNode = data.getNode();
                if (barNode != null) {
                    barNode.setStyle("-fx-bar-fill: " + lineColour + ";");
                }
            }
        }

        for (XYChart.Series<String, Number> series : gasExpenseChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node barNode = data.getNode();
                if (barNode != null) {
                    barNode.setStyle("-fx-bar-fill: " + lineColour + ";");
                }
            }
        }

        for (XYChart.Series<String, Number> series : otherExpenseChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node barNode = data.getNode();
                if (barNode != null) {
                    barNode.setStyle("-fx-bar-fill: " + lineColour + ";");
                }
            }
        }

        for (Node legendSymbol : priceChart.lookupAll(".chart-legend-item-symbol")) {
            legendSymbol.setStyle("-fx-background-color: " + lineColour + ", " + lineColour + ";");
        }
        for (Node legendSymbol : expenseChart.lookupAll(".chart-legend-item-symbol")) {
            legendSymbol.setStyle("-fx-background-color: " + barColour + ", " + barColour + ";");
        }
        for (Node legendSymbol : electricityExpenseChart.lookupAll(".chart-legend-item-symbol")) {
            legendSymbol.setStyle("-fx-background-color: " + barColour + ", " + barColour + ";");
        }
        for (Node legendSymbol : gasExpenseChart.lookupAll(".chart-legend-item-symbol")) {
            legendSymbol.setStyle("-fx-background-color: " + barColour + ", " + barColour + ";");
        }
        for (Node legendSymbol : otherExpenseChart.lookupAll(".chart-legend-item-symbol")) {
            legendSymbol.setStyle("-fx-background-color: " + barColour + ", " + barColour + ";");
        }
    }

    @FXML
    private void onLogUsage() {
        double usage = Double.parseDouble(logUsageField.getText());
        vm.logUsage(usage);
        vm.loadMockExpenses();
    }
}
