package uk.ac.soton.comp2300.group42.energyclient.presentation.controller.debug;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.ColorVisionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.EnergyUsageRect;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.debug.DashboardDebugViewModel;

import java.time.LocalTime;

public class DashboardDebugController {

    private final DashboardDebugViewModel vm;

    public StackPane root;
    public VBox mainContentArea;

    @FXML private EnergyUsageRect energyUsageRect;
    @FXML private Label counterLabel;
    @FXML private Label costLabel;
    @FXML private Label goalLabel;
    @FXML private ComboBox<ColorVision> colorVisionComboBox;

    @FXML private TextField costGoalField;
    @FXML private DatePicker datePicker;
    @FXML private Spinner<LocalTime> timeSpinner;

    @Inject public DashboardDebugController(DashboardDebugViewModel vm) {
        this.vm = vm;
    }

    @FXML private void initialize() {
        energyUsageRect.usageProperty().bind(vm.usageProperty());
        energyUsageRect.fillProperty().bind(vm.visionProperty().map(ColorVisionManager::getGradientFor));
        counterLabel.textProperty().bind(vm.counterProperty().asString());
        costLabel.textProperty().bind(vm.costMessageProperty());
        goalLabel.textProperty().bind(vm.goalMessageProperty());

        colorVisionComboBox.setItems(vm.getAvailableVisions());
        colorVisionComboBox.valueProperty().bindBidirectional(vm.visionProperty());

        costGoalField.textProperty().bindBidirectional(vm.costGoalInputProperty());
        datePicker.valueProperty().bindBidirectional(vm.resetDateProperty());

        SpinnerValueFactory<LocalTime> valueFactory = new SpinnerValueFactory<>() {
            { setValue(vm.resetTimeProperty().get()); }
            @Override public void decrement(int steps) { setValue(getValue().minusMinutes(steps)); }
            @Override public void increment(int steps) { setValue(getValue().plusMinutes(steps)); }
        };
        timeSpinner.setValueFactory(valueFactory);
        valueFactory.valueProperty().bindBidirectional(vm.resetTimeProperty());

        vm.hasCostGoalErrorProperty().subscribe(hasError ->
            costGoalField.setStyle(hasError
                    ? "-fx-border-color: " + ColorVisionManager.getWebColor(ColorVisionManager.ColorRole.VALIDATION_ERROR) + ";"
                    : "")
        );
    }

    @FXML private void onIncrement() { vm.incrementCounter(); }
    @FXML private void onDecrement() { vm.decrementCounter(); }
    @FXML private void onSetCostGoal() { vm.updateCostGoal(); }
    @FXML private void onScheduleReset() { vm.scheduleReset(); }
    @FXML private void onExit() { Navigator.goTo("Landing.fxml"); }
}
