package uk.ac.soton.comp2300.group42.energyclient.presentation.controller.debug;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;
import uk.ac.soton.comp2300.group42.energyclient.presentation.model.ColorVisionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.EnergyUsageRect;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.debug.DashboardDebugViewModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Inject public DashboardDebugController(DashboardDebugViewModel vm) { this.vm = vm; }

    @FXML private void initialize() {
        energyUsageRect.usageProperty().bind(vm.usageProperty());
        energyUsageRect.fillProperty().bind(vm.getPreferences().visionProperty().map(ColorVisionManager::getGradientFor));
        counterLabel.textProperty().bind(vm.counterProperty().asString());
        costLabel.textProperty().bind(vm.costMessageProperty());
        goalLabel.textProperty().bind(vm.goalMessageProperty());

        colorVisionComboBox.getItems().setAll(ColorVision.values());
        colorVisionComboBox.valueProperty().bindBidirectional(vm.getPreferences().visionProperty());

        LocalTime now = LocalTime.now().withSecond(0).withNano(0);

        SpinnerValueFactory<LocalTime> valueFactory =
                new SpinnerValueFactory<>() {
                    {
                        setValue(now);
                    }

                    @Override
                    public void decrement(int steps) {
                        setValue(getValue().minusMinutes(steps));
                    }

                    @Override
                    public void increment(int steps) {
                        setValue(getValue().plusMinutes(steps));
                    }
                };

        timeSpinner.setValueFactory(valueFactory);

    }


    @FXML private void onIncrement() {
        vm.incrementCounter();
    }

    @FXML private void onDecrement() {
        vm.decrementCounter();
    }

    @FXML private void onSetCostGoal() {
        try {
            String text = costGoalField.getText().replace("£", "");
            double value = Double.parseDouble(text);

            if (value <= 0) throw new NumberFormatException();

            vm.setCostGoal(value);
            costGoalField.clear();
            costGoalField.setStyle("");

        } catch (NumberFormatException e) {
            costGoalField.setStyle(
                    "-fx-border-color: " + ColorVisionManager.getWebColor(ColorVisionManager.ColorRole.VALIDATION_ERROR) + ";"
            );
        }
    }

    @FXML private void onScheduleReset() {
        LocalDate date = datePicker.getValue();
        LocalTime time = timeSpinner.getValue();


        if (date == null || time == null) {
            return;
        }

        LocalDateTime resetTime = LocalDateTime.of(date, time);
        System.out.println("Reset scheduled for: " + resetTime);

        vm.scheduleReset(resetTime);
    }

    @FXML private void onExit() {
        Navigator.goTo("Landing.fxml");
    }
}
