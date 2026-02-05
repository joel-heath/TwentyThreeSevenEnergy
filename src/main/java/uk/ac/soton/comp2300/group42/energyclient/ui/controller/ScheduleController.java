package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;

import uk.ac.soton.comp2300.group42.energyclient.ui.model.ApplianceModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.view.components.ActivationSchedulePane;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.ScheduleViewModel;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ScheduleController {

    @FXML private ActivationSchedulePane schedulePane;
    @FXML private Label responseLabel;
    @FXML private ComboBox<String> repeatComboBox;

    private final ScheduleViewModel vm;

    public ScheduleController(ScheduleViewModel vm) { this.vm = vm; }

    @FXML private void initialize() {
        schedulePane.setApplianceList(vm.getApplianceList());
        schedulePane.selectedApplianceProperty().bindBidirectional(vm.selectedApplianceProperty());

        repeatComboBox.setItems(FXCollections.observableArrayList(
                "Does not repeat",
                "Every day",
                "Every week",
                "Every month",
                "Every year",
                "Custom"
        ));

        repeatComboBox.setValue("Does not repeat");

        repeatComboBox.setOnAction(event -> {
            String selected = repeatComboBox.getValue();
            System.out.println("User selected: " + selected);
        });
    }

    @FXML private void onSchedule() {
        ApplianceModel appliance = schedulePane.getSelectedAppliance();
        int hour = schedulePane.getHour();
        int minute = schedulePane.getMinute();

        if (appliance == null) {
            responseLabel.setText("Failed to schedule, no appliance selected");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetDateTime = LocalDateTime.of(now.toLocalDate(), LocalTime.of(hour, minute));

        boolean isBeforeOrNow = targetDateTime.isBefore(now) || targetDateTime.isEqual(now);
        if (isBeforeOrNow) targetDateTime = targetDateTime.plusDays(1);

        vm.scheduleActivation(targetDateTime);

        String formattedTime = targetDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        responseLabel.setText(appliance.getName() + " scheduled for " +
                (isBeforeOrNow ? "tomorrow at " : "") +
                formattedTime);
    }
}
