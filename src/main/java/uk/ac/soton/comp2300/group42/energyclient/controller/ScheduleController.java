package uk.ac.soton.comp2300.group42.energyclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import uk.ac.soton.comp2300.group42.energyclient.view.components.ActivationSchedulePane;
import uk.ac.soton.comp2300.group42.energyclient.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.viewmodel.ScheduleViewModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScheduleController {

    @FXML private ActivationSchedulePane schedulePane;
    @FXML private Label responseLabel;

    private final ScheduleViewModel vm;

    public ScheduleController(ScheduleViewModel vm) { this.vm = vm; }

    @FXML private void initialize() {
        schedulePane.setApplianceList(vm.getApplianceList());
        schedulePane.selectedApplianceProperty().bindBidirectional(vm.selectedApplianceProperty());
        schedulePane.setOnScheduleAction((appliance, time) -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime targetDateTime = LocalDateTime.of(now.toLocalDate(), time);

            boolean isBeforeOrNow = targetDateTime.isBefore(now) || targetDateTime.isEqual(now);
            if (isBeforeOrNow) targetDateTime = targetDateTime.plusDays(1);

            vm.scheduleActivation(targetDateTime);

            String formattedTime = targetDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            responseLabel.setText(appliance.getName() + " scheduled for " +
                    (isBeforeOrNow ? "tomorrow at " : "") +
                    formattedTime);
        });
    }

    public void onBack() {
        Navigator.goTo("dashboard.fxml");
    }
}
