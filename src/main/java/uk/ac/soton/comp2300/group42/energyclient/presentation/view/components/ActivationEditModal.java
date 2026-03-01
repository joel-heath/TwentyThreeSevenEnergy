package uk.ac.soton.comp2300.group42.energyclient.presentation.view.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableActivation;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.UpcomingActivationsViewModel;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ActivationEditModal extends Modal {

    @FXML private ActivationSchedulePane schedulePane;
    @FXML private Label responseLabel;

    private UpcomingActivationsViewModel vm;
    private ObservableActivation currentEditingActivation;

    public void bindComponents(UpcomingActivationsViewModel vm) {
        this.vm = vm;

        schedulePane.setApplianceList(vm.getAppliances());
    }

    public ActivationEditModal() throws IOException  {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ActivationEditModal.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }

    @Override
    public void close() {
        super.close();
        this.currentEditingActivation = null;
    }

    @Override
    public void show() {
        if (currentEditingActivation == null)
            throw new IllegalStateException("No activation set for editing. Use show(ActivationModel) to set an activation before showing the modal.");

        super.show();
    }

    public void show(ObservableActivation activation) {
        this.currentEditingActivation = activation;

        schedulePane.setSelectedAppliance(activation.getAppliance());
        schedulePane.setHour(activation.getActivationTime().getHour());
        schedulePane.setMinute(activation.getActivationTime().getMinute());
        schedulePane.setDate(activation.getActivationDate() == null ? LocalDateTime.now().toLocalDate() : activation.getActivationDate());
        schedulePane.setRecurrenceRulesVisible(activation.isRecurring());
        schedulePane.setRecursMonday(activation.isRecursMonday());
        schedulePane.setRecursTuesday(activation.isRecursTuesday());
        schedulePane.setRecursWednesday(activation.isRecursWednesday());
        schedulePane.setRecursThursday(activation.isRecursThursday());
        schedulePane.setRecursFriday(activation.isRecursFriday());
        schedulePane.setRecursSaturday(activation.isRecursSaturday());
        schedulePane.setRecursSunday(activation.isRecursSunday());

        super.show();
    }
    private boolean guard(boolean condition, String errorMessage) {
        if (condition)
            responseLabel.setText(errorMessage);
        return condition;
    }

    @FXML private void onSaveActivation() {
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

        vm.updateActivation(currentEditingActivation,
                appliance,
                LocalTime.of(hour, minute),
                schedulePane.getDate(),
                schedulePane.isRecursMonday(), schedulePane.isRecursTuesday(),
                schedulePane.isRecursWednesday(), schedulePane.isRecursThursday(),
                schedulePane.isRecursFriday(), schedulePane.isRecursSaturday(),
                schedulePane.isRecursSunday(),
                schedulePane.isRecurrenceRulesVisible());

        this.close();
    }

    @FXML private void onCancelActivation() {
        vm.removeActivation(currentEditingActivation);
        this.close();
    }
}
