package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.ProgressTrackingViewModel;


public class ProgressTrackingController {
    @FXML private LineChart<String, Number> priceChart;
    @FXML private BarChart<String, Number> expenseChart;

    private final ProgressTrackingViewModel vm = new ProgressTrackingViewModel();

    @FXML private Label priceLabel;

    @FXML
    public void initialize() {
        priceChart.setData(vm.getPriceSeriesData());
        priceLabel.textProperty().bind(
            vm.currentPriceProperty().asString("%.2f p/kWh")
        );
        vm.loadData();
    }


}
