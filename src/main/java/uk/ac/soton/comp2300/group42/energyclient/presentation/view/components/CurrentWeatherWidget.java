package uk.ac.soton.comp2300.group42.energyclient.presentation.view.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.CurrentWeatherViewModel;

import java.io.IOException;

public class CurrentWeatherWidget extends VBox {

    private static final String WIDGET_STYLE = "-fx-background-radius: 10; -fx-padding: 15; -fx-font-weight: bold";

    @FXML private Label tempLabel;
    @FXML private Label sunlightLabel;

    public void bindComponents(CurrentWeatherViewModel vm) {
        tempLabel.textProperty().bind(vm.temperatureProperty());
        sunlightLabel.textProperty().bind(vm.sunlightIntensityProperty());

        vm.refresh();
    }

    public CurrentWeatherWidget() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CurrentWeatherWidget.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }
}
