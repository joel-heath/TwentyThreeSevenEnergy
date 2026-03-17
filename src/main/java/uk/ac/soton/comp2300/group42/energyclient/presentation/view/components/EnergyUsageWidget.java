package uk.ac.soton.comp2300.group42.energyclient.presentation.view.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.EnergyUsageViewModel;

import java.io.IOException;

public class EnergyUsageWidget extends VBox {
    @FXML private EnergyUsageRect energyUsageRect;
    @FXML private Label totalSpentLabel;
    @FXML private Label costLabel;
    @FXML private Label goalLabel;

    public void bindComponents(EnergyUsageViewModel vm) {
        costLabel.textProperty().unbind();
        goalLabel.textProperty().unbind();
        energyUsageRect.usageProperty().unbind();
        energyUsageRect.fillProperty().unbind();
        energyUsageRect.effectProperty().unbind();

        costLabel.textProperty().bind(vm.costMessageProperty());
        goalLabel.textProperty().bind(vm.goalMessageProperty());
        energyUsageRect.usageProperty().bind(vm.usageProperty());

        energyUsageRect.usageProperty().addListener((_, _, newVal) -> updateUsageState(newVal.doubleValue()));
        updateUsageState(vm.usageProperty().get());
    }

    public EnergyUsageWidget() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EnergyUsageWidget.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }

    private void updateUsageState(double usage) {
        if (usage >= 2.0) {
            energyUsageRect.setUsageState(EnergyUsageRect.UsageState.CRITICAL);
        } else if (usage >= 1.5) {
            energyUsageRect.setUsageState(EnergyUsageRect.UsageState.WARNING);
        } else {
            energyUsageRect.setUsageState(EnergyUsageRect.UsageState.NORMAL);
        }
    }
}
