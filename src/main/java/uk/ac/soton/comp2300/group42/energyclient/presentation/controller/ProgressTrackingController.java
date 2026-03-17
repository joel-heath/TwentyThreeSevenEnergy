package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.ProgressTrackingViewModel;

public class ProgressTrackingController {

    @FXML private LineChart<String, Number> priceChart;
    @FXML private BarChart<String, Number> expenseChart;
    @FXML private Label priceLabel;
    @FXML private TextField logUsageField;

    private final ProgressTrackingViewModel vm;
    @Inject public ProgressTrackingController(ProgressTrackingViewModel vm) { this.vm = vm; }
    private final BooleanProperty loadError = new SimpleBooleanProperty(false);

    @FXML private void initialize() {
        priceChart.setData(vm.getPriceSeriesData());
        expenseChart.setData(vm.getExpenseSeriesData());

        priceLabel.textProperty().bind(
            vm.currentPriceProperty().asString("%.2f p/kWh")
        );
        priceLabel.getStyleClass().add("price-label");
        loadError.addListener((_, _, _) -> updatePriceLabelState());
        updatePriceLabelState();

        vm.loadMockExpenses(); // when real data is available, do this asynchronously
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

    private void updatePriceLabelState() {
        var classes = priceLabel.getStyleClass();
        if (loadError.get()) {
            if (!classes.contains("error-text")) {
                classes.add("error-text");
            }
        } else {
            classes.remove("error-text");
        }
    }

    @FXML
    private void onLogUsage() {
        double usage = Double.parseDouble(logUsageField.getText());
        vm.logUsage(usage);
        vm.loadMockExpenses();
    }
}
