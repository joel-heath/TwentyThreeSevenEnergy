package uk.ac.soton.comp2300.group42.energyclient.ui.view.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ColorSettings;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.EnergyUsageViewModel;

import java.io.IOException;

public class EnergyUsageWidget extends VBox {

    @FXML private EnergyUsageRect energyUsageRect;
    @FXML private Label costLabel;
    @FXML private Label goalLabel;

    public void bindComponents(EnergyUsageViewModel vm) {
        costLabel.textProperty().unbind();
        goalLabel.textProperty().unbind();
        energyUsageRect.usageProperty().unbind();
        energyUsageRect.fillProperty().unbind();

        costLabel.textProperty().bind(vm.costMessageProperty());
        goalLabel.textProperty().bind(vm.goalMessageProperty());
        energyUsageRect.usageProperty().bind(vm.usageProperty());
        energyUsageRect.fillProperty().bind(
                vm.getPreferences().visionProperty().map(ColorSettings::getGradientFor)
        );
    }

    public EnergyUsageWidget() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EnergyUsageWidget.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }
}
