package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.ProgressTrackingViewModel;

public class ProgressTrackingController {
    @FXML
    private LineChart<String, Number> priceChart;
    @FXML private BarChart<String, Number> expenseChart;

    private final ProgressTrackingViewModel viewModel = new ProgressTrackingViewModel();

    @FXML
    public void initialize() {
        priceChart.setData(viewModel.getPriceSeriesData());

        viewModel.loadData();
    }
}
