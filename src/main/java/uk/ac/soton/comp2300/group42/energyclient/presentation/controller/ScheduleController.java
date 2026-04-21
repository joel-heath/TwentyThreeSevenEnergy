package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import uk.ac.soton.comp2300.group42.energyclient.presentation.util.StyleClassUtils;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.ActivationSchedulePane;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.ScheduleViewModel;

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

        responseLabel.textProperty().bind(vm.responseMessageProperty());
        StyleClassUtils.bindExclusiveClass(responseLabel, vm.responseStyleClassProperty(), "response-error", "response-success");

        vm.loadData();
    }

    @FXML private void onSchedule() {
        vm.scheduleActivation();
    }
}
