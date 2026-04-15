package uk.ac.soton.comp2300.group42.energyclient.presentation.view.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableActivation;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.ColorVisionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.ActivationEditViewModel;

import java.io.IOException;

public class ActivationEditModal extends Modal {

    @FXML private ActivationSchedulePane schedulePane;
    @FXML private Label responseLabel;

    private ActivationEditViewModel vm;

    public void bindComponents(ActivationEditViewModel vm) {
        this.vm = vm;

        schedulePane.selectedApplianceProperty().bindBidirectional(vm.selectedApplianceProperty());
        schedulePane.hourProperty().bindBidirectional(vm.hourProperty());
        schedulePane.minuteProperty().bindBidirectional(vm.minuteProperty());
        schedulePane.dateProperty().bindBidirectional(vm.dateProperty());

        schedulePane.recursMondayProperty().bindBidirectional(vm.recursMondayProperty());
        schedulePane.recursTuesdayProperty().bindBidirectional(vm.recursTuesdayProperty());
        schedulePane.recursWednesdayProperty().bindBidirectional(vm.recursWednesdayProperty());
        schedulePane.recursThursdayProperty().bindBidirectional(vm.recursThursdayProperty());
        schedulePane.recursFridayProperty().bindBidirectional(vm.recursFridayProperty());
        schedulePane.recursSaturdayProperty().bindBidirectional(vm.recursSaturdayProperty());
        schedulePane.recursSundayProperty().bindBidirectional(vm.recursSundayProperty());
        schedulePane.recurrenceRulesVisibleProperty().bindBidirectional(vm.isRecurringProperty());

        responseLabel.textProperty().bind(vm.responseMessageProperty());
        vm.responseRoleProperty().subscribe(newVal ->
                responseLabel.setTextFill(ColorVisionManager.getColor(newVal))
        );
    }

    public ActivationEditModal() throws IOException  {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ActivationEditModal.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }

    public void show(ObservableActivation activation) {
        if (vm == null)
            throw new IllegalStateException("Must call bindComponents(ActivationEditViewModel vm) before showing the modal.");

        vm.loadActivation(activation);
        super.show();
    }

    @FXML private void onSaveActivation() {
        if (vm.saveChanges())
            this.close();
    }

    @FXML private void onCancelActivation() {
        vm.deleteActivation();
        this.close();
    }
}
