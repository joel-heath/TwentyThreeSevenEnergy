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
        costLabel.textProperty().bind(vm.costMessageProperty());
        goalLabel.textProperty().bind(vm.goalMessageProperty());
        energyUsageRect.usageProperty().bind(vm.usageProperty());
        applyUsageState(vm.usageStateProperty().get());
        vm.usageStateProperty().subscribe(this::applyUsageState);
    }

    public EnergyUsageWidget() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EnergyUsageWidget.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }

    private void applyUsageState(EnergyUsageViewModel.UsageState state) {
        if (state == null) {
            energyUsageRect.setUsageState(EnergyUsageRect.UsageState.NORMAL);
            return;
        }

        switch (state) {
            case NORMAL -> energyUsageRect.setUsageState(EnergyUsageRect.UsageState.NORMAL);
            case WARNING -> energyUsageRect.setUsageState(EnergyUsageRect.UsageState.WARNING);
            case CRITICAL -> energyUsageRect.setUsageState(EnergyUsageRect.UsageState.CRITICAL);
        }
    }
}
