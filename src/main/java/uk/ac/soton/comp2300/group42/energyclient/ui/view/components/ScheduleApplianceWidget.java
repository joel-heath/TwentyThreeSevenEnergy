package uk.ac.soton.comp2300.group42.energyclient.ui.view.components;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ActivationModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ApplianceModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.ScheduleApplianceWidgetViewModel;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import static uk.ac.soton.comp2300.group42.energyclient.ui.util.ControllerUtils.formatDay;

public class ScheduleApplianceWidget extends VBox {
    @FXML private HBox scheduleContainer;

    private final ScheduleApplianceWidgetViewModel vm;
    private final Modal editModal;
    private final ActivationSchedulePane schedulePane;
    private final Label responseLabel;
    private ActivationModel currentEditingActivation;

    public ScheduleApplianceWidget(ScheduleApplianceWidgetViewModel vm, Modal editModal,
                                   ActivationSchedulePane schedulePane, Label responseLabel) {
        this.vm = vm;
        this.editModal = editModal;
        this.schedulePane = schedulePane;
        this.responseLabel = responseLabel;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ScheduleApplianceWidget.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML private void initialize() {
        schedulePane.setApplianceList(vm.getAppliances());
        bindActivations();
    }

    @FXML private void onSchedule() {
        Navigator.goTo("Schedule.fxml");
    }

    private void openEditModal(ActivationModel activation) {
        this.currentEditingActivation = activation;

        schedulePane.selectedApplianceProperty().setValue(activation.getAppliance());
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

        editModal.show();
    }

    @FXML public void onCloseEditModal() {
        editModal.close();
        this.currentEditingActivation = null;
    }

    private boolean guard(boolean condition, String errorMessage) {
        if (condition)
            responseLabel.setText(errorMessage);
        return condition;
    }

    @FXML public void onSaveActivation() {
        ApplianceModel appliance = schedulePane.getSelectedAppliance();
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
        onCloseEditModal();
    }

    @FXML public void onCancelActivation() {
        vm.removeActivation(currentEditingActivation);
        onCloseEditModal();
    }

    private Pane createActivationView(ActivationModel activation) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: lightblue; -fx-background-radius: 5; -fx-padding: 5; -fx-spacing: 5");

        Label nameLabel = new Label();
        Label timeLabel = new Label();
        Label dateLabel = new Label();

        nameLabel.textProperty().bind(
                activation.applianceProperty().flatMap(ApplianceModel::nameProperty)
        );
        timeLabel.textProperty().bind(Bindings.createStringBinding(
                () -> activation.getActivationTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                activation.activationDateProperty()
        ));
        dateLabel.textProperty().bind(Bindings.createStringBinding(
                () -> formatDay(activation.getNextActivationDateTime()),
                activation.activationTimeProperty(),
                activation.activationDateProperty(),
                activation.recursMondayProperty(),
                activation.recursTuesdayProperty(),
                activation.recursWednesdayProperty(),
                activation.recursThursdayProperty(),
                activation.recursFridayProperty(),
                activation.recursSaturdayProperty(),
                activation.recursSundayProperty()
        ));
        card.getChildren().addAll(nameLabel, timeLabel, dateLabel);
        card.setOnMouseClicked(_ -> this.openEditModal(activation));
        card.setUserData(activation);

        return card;
    }

    private void bindActivations() {
        SortedList<ActivationModel> activations = vm.getActivations();

        scheduleContainer.getChildren().clear();
        for (ActivationModel activation : activations) {
            scheduleContainer.getChildren().add(createActivationView(activation));
        }

        activations.addListener((ListChangeListener<ActivationModel>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (int i = 0; i < change.getAddedSize(); i++) {
                        ActivationModel addedItem = change.getAddedSubList().get(i);
                        Node view = createActivationView(addedItem);
                        scheduleContainer.getChildren().add(change.getFrom() + i, view);
                    }
                }

                if (change.wasRemoved()) {
                    scheduleContainer.getChildren().remove(
                            change.getFrom(),
                            change.getFrom() + change.getRemovedSize()
                    );
                }

                if (change.wasPermutated() || change.wasUpdated()) {
                    FXCollections.sort(scheduleContainer.getChildren(),
                            Comparator.comparingInt(node -> activations.indexOf((ActivationModel) node.getUserData())));
                }
            }
        });
    }
}
