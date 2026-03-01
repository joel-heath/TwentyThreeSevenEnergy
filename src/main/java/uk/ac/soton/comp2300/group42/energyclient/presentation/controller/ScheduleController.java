package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.ActivationSchedulePane;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.ScheduleViewModel;

import java.time.LocalDateTime;

import static uk.ac.soton.comp2300.group42.energyclient.presentation.util.ControllerUtils.formatDay;

public class ScheduleController {

    @FXML private ActivationSchedulePane schedulePane;
    @FXML private Label responseLabel;

    private final ScheduleViewModel vm;

    @Inject public ScheduleController(ScheduleViewModel vm) { this.vm = vm; }

    @FXML private void initialize() {
        schedulePane.setApplianceList(vm.getApplianceList());
        vm.selectedApplianceProperty().bind(schedulePane.selectedApplianceProperty());
        vm.hourProperty().bind(schedulePane.hourProperty());
        vm.minuteProperty().bind(schedulePane.minuteProperty());
        vm.dateProperty().bind(schedulePane.dateProperty());
        vm.recursMondayProperty().bind(schedulePane.recursMondayProperty());
        vm.recursTuesdayProperty().bind(schedulePane.recursTuesdayProperty());
        vm.recursWednesdayProperty().bind(schedulePane.recursWednesdayProperty());
        vm.recursThursdayProperty().bind(schedulePane.recursThursdayProperty());
        vm.recursFridayProperty().bind(schedulePane.recursFridayProperty());
        vm.recursSaturdayProperty().bind(schedulePane.recursSaturdayProperty());
        vm.recursSundayProperty().bind(schedulePane.recursSundayProperty());
        vm.isRecurringProperty().bind(schedulePane.recurrenceRulesVisibleProperty());
    }

    private boolean guard(boolean condition, String errorMessage) {
        if (condition)
            responseLabel.setText(errorMessage);
        return condition;
    }

    @FXML private void onSchedule() {
        ObservableAppliance appliance = schedulePane.getSelectedAppliance();
        int hour = schedulePane.getHour();
        int minute = schedulePane.getMinute();
        boolean recurs = schedulePane.isRecurrenceRulesVisible();

        if (guard(appliance == null,
                  "Failed to schedule, no appliance selected") ||
            guard(recurs && !schedulePane.isRecursSet(),
                  "Failed to schedule, no recurrence days selected") ||
            guard(!recurs && schedulePane.getDate().isBefore(LocalDateTime.now().toLocalDate()),
                  "Failed to schedule, selected date is in the past")
        ) return;

        LocalDateTime time = vm.scheduleActivation();

        assert appliance != null;
        responseLabel.setText(appliance.getName() + " scheduled for " +
                              String.format("%02d", hour) + ":" + String.format("%02d", minute) + " on " +
                              formatDay(time));
    }
}
