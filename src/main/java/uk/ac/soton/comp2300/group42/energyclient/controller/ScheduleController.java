package uk.ac.soton.comp2300.group42.energyclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import uk.ac.soton.comp2300.group42.energyclient.model.entity.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.services.NotificationService;
import uk.ac.soton.comp2300.group42.energyclient.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.viewmodel.ScheduleViewModel;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class ScheduleController {

    @FXML private ComboBox<Appliance> applianceSelector;
    @FXML private Spinner<Integer> hourSpinner;
    @FXML private Spinner<Integer> minuteSpinner;

    private final ScheduleViewModel vm = new ScheduleViewModel();

    public void initialize() {
        applianceSelector.setItems(vm.getApplianceList());
        applianceSelector.valueProperty().bindBidirectional(vm.selectedApplianceProperty());

        LocalTime now = LocalTime.now();
        var hourFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, now.getHour());
        hourFactory.setWrapAround(true);
        var minuteFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, now.getMinute());
        minuteFactory.setWrapAround(true);

        hourSpinner.setValueFactory(hourFactory);
        minuteSpinner.setValueFactory(minuteFactory);
    }

    public void onBack() {
        Navigator.goTo("dashboard.fxml");
    }

    public void onSchedule() {
        Appliance selected = vm.getSelectedAppliance();
        Integer hour = hourSpinner.getValue();
        Integer minute = minuteSpinner.getValue();

        if (selected != null && hour != null && minute != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime targetDateTime = LocalDateTime.of(now.toLocalDate(), LocalTime.of(hour, minute));

            // if they schedule it for a time in the past assume they wanted tomorrow.
            if (targetDateTime.isBefore(now) || targetDateTime.isEqual(now))
                targetDateTime = targetDateTime.plusDays(1);

            NotificationService.scheduleNotification(selected.getName(), targetDateTime);

            System.out.println("Scheduled for: " + targetDateTime); // TODO: Prompt the user that the task has successfully scheduled
        }
        else
        {
            // TODO: Tell the user failed to schedule
        }
    }

}
