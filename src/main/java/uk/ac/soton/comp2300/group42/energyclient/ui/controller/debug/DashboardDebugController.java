package uk.ac.soton.comp2300.group42.energyclient.ui.controller.debug;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.EnergyCalculator;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.ui.view.components.EnergyUsageRect;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.debug.DashboardDebugViewModel;


public class DashboardDebugController {

    public StackPane root;
    public VBox mainContentArea;

    @FXML private EnergyUsageRect energyUsageController;
    @FXML private Label counterLabel;
    @FXML private Label costLabel;
    @FXML private Label goalLabel;

    @FXML private final DashboardDebugViewModel vm = new DashboardDebugViewModel(new EnergyCalculator());

    @FXML
    private void initialize() {
        energyUsageController.usageProperty().bind(vm.usageProperty());
        counterLabel.textProperty().bind(vm.counterProperty().asString());
        costLabel.textProperty().bind(vm.costMessageProperty());
        goalLabel.textProperty().bind(vm.goalMessageProperty());
    }

    @FXML
    private void onIncrement() {
        vm.incrementCounter();
    }

    @FXML
    private void onDecrement() {
        vm.decrementCounter();
    }


    @FXML
    private void onExit() {
        Navigator.goTo("landing.fxml");
    }


}
