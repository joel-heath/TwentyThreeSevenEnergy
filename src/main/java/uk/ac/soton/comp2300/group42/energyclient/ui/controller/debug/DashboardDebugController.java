package uk.ac.soton.comp2300.group42.energyclient.ui.controller.debug;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ColorVision;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ColorSettings;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.ui.view.components.EnergyUsageRect;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.debug.DashboardDebugViewModel;


public class DashboardDebugController {

    private final DashboardDebugViewModel vm;

    public StackPane root;
    public VBox mainContentArea;

    @FXML private EnergyUsageRect energyUsageController;
    @FXML private Label counterLabel;
    @FXML private Label costLabel;
    @FXML private Label goalLabel;
    @FXML private ComboBox<ColorVision> colorVisionComboBox;

    @FXML private TextField costGoalField;

    public DashboardDebugController(DashboardDebugViewModel vm) { this.vm = vm; }

    @FXML
    private void initialize() {
        energyUsageController.usageProperty().bind(vm.usageProperty());
        counterLabel.textProperty().bind(vm.counterProperty().asString());
        costLabel.textProperty().bind(vm.costMessageProperty());
        goalLabel.textProperty().bind(vm.goalMessageProperty());

        colorVisionComboBox.getItems().setAll(ColorVision.values());
        colorVisionComboBox.valueProperty().bindBidirectional(vm.getPreferences().visionProperty());

        energyUsageController.setFillProperty(ColorSettings.getGradientFor(vm.getPreferences().getVision()));
        vm.getPreferences().visionProperty().addListener((_, _, mode) ->
                energyUsageController.setFillProperty(ColorSettings.getGradientFor(mode))
        );
    }


    @FXML
    private void onIncrement() {
        vm.incrementCounter();
    }

    @FXML
    private void onDecrement() {
        vm.decrementCounter();
    }

    @FXML private void onSetCostGoal() {
        try {
            double value = Double.parseDouble(costGoalField.getText());

            if (value <= 0) throw new NumberFormatException();

            vm.setCostGoal(value);
            costGoalField.clear();
            costGoalField.setStyle("");

        } catch (NumberFormatException e) {
            costGoalField.setStyle("-fx-border-color: red;");
        }
    }

    @FXML
    private void onExit() {
        Navigator.goTo("landing.fxml");
    }


}
