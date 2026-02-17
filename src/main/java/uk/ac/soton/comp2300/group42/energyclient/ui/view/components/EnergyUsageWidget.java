package uk.ac.soton.comp2300.group42.energyclient.ui.view.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ColorSettings;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.EnergyUsageWidgetViewModel;

import java.io.IOException;

public class EnergyUsageWidget extends VBox {
    @FXML private EnergyUsageRect energyUsageRect;
    @FXML private Label costLabel;
    @FXML private Label goalLabel;

    private final EnergyUsageWidgetViewModel vm;

    public EnergyUsageWidget(EnergyUsageWidgetViewModel vm) {
        this.vm = vm;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EnergyUsageWidget.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML private void initialize() {
        costLabel.textProperty().bind(vm.costMessageProperty());
        goalLabel.textProperty().bind(vm.goalMessageProperty());
        energyUsageRect.usageProperty().bind(vm.usageProperty());
        energyUsageRect.fillProperty().bind(
                vm.getPreferences().visionProperty().map(ColorSettings::getGradientFor)
        );
    }
}
