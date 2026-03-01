package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.ColorVisionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.ProgressTrackingViewModel;

public class ProgressTrackingController {

    @FXML private LineChart<String, Number> priceChart;
    @FXML private BarChart<String, Number> expenseChart;

    private final ProgressTrackingViewModel vm;
    @Inject public ProgressTrackingController(ProgressTrackingViewModel vm) { this.vm = vm; }

    @FXML private Label priceLabel;

    @FXML private void initialize() {
        priceChart.setData(vm.getPriceSeriesData());
        expenseChart.setData(vm.getExpenseSeriesData());
        priceLabel.textProperty().bind(
            vm.currentPriceProperty().asString("%.2f p/kWh")
        );

        vm.loadMockExpenses(); // when real data is available, do this asynchronously

        vm.loadDataAsync().exceptionally(e -> {
            Platform.runLater(() -> {
                priceLabel.setText("Failed to load data.");
                priceLabel.setStyle("-fx-text-fill: " + ColorVisionManager.getWebColor(ColorVisionManager.ColorRole.VALIDATION_ERROR) + ";");
            });
            System.out.println("Error loading price data: " + e.getMessage());
            return null;
        });
    }
}
