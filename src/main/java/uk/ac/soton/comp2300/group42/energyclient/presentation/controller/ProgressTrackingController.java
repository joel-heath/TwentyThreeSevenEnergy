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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import uk.ac.soton.comp2300.group42.common.EnergyCategory;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.ColorVisionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.ProgressTrackingViewModel;

import static uk.ac.soton.comp2300.group42.energyclient.presentation.util.ControllerUtils.createConverter;

public class ProgressTrackingController {

    @FXML private LineChart<String, Number> priceChart;
    @FXML private BarChart<String, Number> usageChart;
    @FXML private BarChart<String, Number> electricityUsageChart;
    @FXML private BarChart<String, Number> gasUsageChart;
    @FXML private BarChart<String, Number> otherUsageChart;

    @FXML private ToggleButton btnElec;
    @FXML private ToggleButton btnGas;
    @FXML private ToggleButton btnOther;

    @FXML private Label priceLabel;
    @FXML private TextField logUsageField;
    @FXML private ComboBox<EnergyCategory> categoryComboBox;

    private final ProgressTrackingViewModel vm;
    @Inject public ProgressTrackingController(ProgressTrackingViewModel vm) { this.vm = vm; }
    private final BooleanProperty loadError = new SimpleBooleanProperty(false);

    @FXML private void initialize() {
        priceChart.setData(vm.getPriceSeriesData());
        usageChart.setData(vm.getExpenseSeriesData());
        electricityUsageChart.setData(vm.getElectricitySeriesData());
        gasUsageChart.setData(vm.getGasSeriesData());
        otherUsageChart.setData(vm.getOtherExpenseSeriesData());

        electricityUsageChart.visibleProperty().bind(btnElec.selectedProperty());
        gasUsageChart.visibleProperty().bind(btnGas.selectedProperty());
        otherUsageChart.visibleProperty().bind(btnOther.selectedProperty());

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

        categoryComboBox.getItems().setAll(EnergyCategory.values());
        categoryComboBox.setConverter(createConverter(EnergyCategory::getName));
        categoryComboBox.valueProperty().bindBidirectional(vm.selectedCategoryProperty());

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

        for (XYChart.Series<String, Number> series : usageChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node barNode = data.getNode();
                if (barNode != null) {
                    barNode.setStyle("-fx-bar-fill: " + barColour + ";");
                }
            }
        }

        for (XYChart.Series<String, Number> series : electricityUsageChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node barNode = data.getNode();
                if (barNode != null) {
                    barNode.setStyle("-fx-bar-fill: " + lineColour + ";");
                }
            }
        }

        for (XYChart.Series<String, Number> series : gasUsageChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node barNode = data.getNode();
                if (barNode != null) {
                    barNode.setStyle("-fx-bar-fill: " + lineColour + ";");
                }
            }
        }

        for (XYChart.Series<String, Number> series : otherUsageChart.getData()) {
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
        for (Node legendSymbol : usageChart.lookupAll(".chart-legend-item-symbol")) {
            legendSymbol.setStyle("-fx-background-color: " + barColour + ", " + barColour + ";");
        }
        for (Node legendSymbol : electricityUsageChart.lookupAll(".chart-legend-item-symbol")) {
            legendSymbol.setStyle("-fx-background-color: " + barColour + ", " + barColour + ";");
        }
        for (Node legendSymbol : gasUsageChart.lookupAll(".chart-legend-item-symbol")) {
            legendSymbol.setStyle("-fx-background-color: " + barColour + ", " + barColour + ";");
        }
        for (Node legendSymbol : otherUsageChart.lookupAll(".chart-legend-item-symbol")) {
            legendSymbol.setStyle("-fx-background-color: " + barColour + ", " + barColour + ";");
        }
    }

    @FXML
    private void onLogUsage() {
        double usage = Double.parseDouble(logUsageField.getText());
        vm.logUsage(usage);
        vm.loadMockExpenses();
        vm.loadExpensesByCategory(vm.selectedCategoryProperty().get());
    }
}
