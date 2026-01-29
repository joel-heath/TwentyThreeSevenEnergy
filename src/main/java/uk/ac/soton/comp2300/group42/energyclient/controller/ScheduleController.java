package uk.ac.soton.comp2300.group42.energyclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import uk.ac.soton.comp2300.group42.energyclient.model.entity.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.services.NotificationService;
import uk.ac.soton.comp2300.group42.energyclient.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.viewmodel.ScheduleViewModel;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ScheduleController {

    @FXML private Label responseLabel;
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

        if (selected == null) {
            responseLabel.setText("Failed to schedule, no appliance selected");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetDateTime = LocalDateTime.of(now.toLocalDate(), LocalTime.of(hour, minute));

        boolean isBeforeOrNow = targetDateTime.isBefore(now) || targetDateTime.isEqual(now);
        if (isBeforeOrNow) targetDateTime = targetDateTime.plusDays(1);

        NotificationService.scheduleNotification(selected.getName(), targetDateTime);
        String formattedTime = targetDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));

        responseLabel.setText(selected + " scheduled for " +
            (isBeforeOrNow ? "tomorrow at " : "") +
            formattedTime);
    }

}
