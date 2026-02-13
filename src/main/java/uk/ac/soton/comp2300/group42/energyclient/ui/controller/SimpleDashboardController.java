package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import uk.ac.soton.comp2300.group42.energyclient.ui.model.ActivationModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ApplianceModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ColorSettings;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.ui.view.components.ActivationSchedulePane;
import uk.ac.soton.comp2300.group42.energyclient.ui.view.components.EnergyUsageRect;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.SimpleDashboardViewModel;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import static uk.ac.soton.comp2300.group42.energyclient.ui.util.ControllerUtils.formatDay;

public class SimpleDashboardController {

    @FXML private VBox mainContentArea;
    @FXML private StackPane editModalOverlay;
    @FXML private ActivationSchedulePane schedulePane;
    @FXML private Label responseLabel;
    private ActivationModel currentEditingActivation;
    private final BoxBlur blur = new BoxBlur(10, 10, 3);

    @FXML private EnergyUsageRect energyUsageRect;
    @FXML private Label costLabel;
    @FXML private Label goalLabel;

    @FXML private HBox scheduleContainer;

    private final SimpleDashboardViewModel vm;

    public SimpleDashboardController(SimpleDashboardViewModel vm) { this.vm = vm; }

    @FXML private void initialize() {
        costLabel.textProperty().bind(vm.costMessageProperty());
        goalLabel.textProperty().bind(vm.goalMessageProperty());
        energyUsageRect.usageProperty().bind(vm.usageProperty());
        energyUsageRect.fillProperty().bind(vm.getPreferences().visionProperty().map(ColorSettings::getGradientFor));
        bindActivations();

        schedulePane.setApplianceList(vm.getAppliances());
        editModalOverlay.setVisible(false);
        editModalOverlay.setOnMouseClicked(e -> {
            if (e.getTarget() == editModalOverlay)
                onCloseEditModal();
        });

        vm.startAutoUpdateTest();
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

        mainContentArea.setEffect(blur);
        editModalOverlay.setVisible(true);
    }

    @FXML private void onCloseEditModal() {
        mainContentArea.setEffect(null);
        editModalOverlay.setVisible(false);
        this.currentEditingActivation = null;
    }

    private boolean guard(boolean condition, String errorMessage) {
        if (condition)
            responseLabel.setText(errorMessage);
        return condition;
    }

    @FXML private void onSaveActivation() {
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
                            schedulePane.isRecursMonday(), schedulePane.isRecursTuesday(), schedulePane.isRecursWednesday(), schedulePane.isRecursThursday(), schedulePane.isRecursFriday(), schedulePane.isRecursSaturday(), schedulePane.isRecursSunday(),
                            schedulePane.isRecurrenceRulesVisible());
        onCloseEditModal();
    }

    @FXML private void onCancelActivation() {
        vm.removeActivation(currentEditingActivation);
        onCloseEditModal();
    }

    @FXML private void onSchedule() {
        Navigator.goTo("Schedule.fxml");
    }

    @FXML private void onManageHouses() {
        Navigator.goTo("ManageHouses.fxml");
    }

    private Pane createActivationView(ActivationModel activation) {
        VBox card = new VBox();
        // card.setPrefSize(100, 150);
        card.setStyle("-fx-background-color: lightblue; -fx-background-radius: 5; -fx-padding: 5; -fx-spacing: 5");

        Label nameLabel = new Label();
        Label timeLabel = new Label();
        Label dateLabel = new Label();

        nameLabel.textProperty().bind(
                activation.applianceProperty().flatMap(ApplianceModel::nameProperty)
        );
        timeLabel.textProperty().bind(Bindings.createStringBinding(
                () -> activation.getActivationTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                activation.activationTimeProperty()
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
        card.setUserData(activation); // for sorting when an activation's time is changed.

        return card;
    }

    private void bindActivations() {
        // `activations` is the live list, the ViewModel passes it on from the ActivationClient
        SortedList<ActivationModel> activations = vm.getActivations();

        scheduleContainer.getChildren().clear();
        for (ActivationModel activation : activations) {
            scheduleContainer.getChildren().add(createActivationView(activation));
        }

        activations.addListener((ListChangeListener<ActivationModel>) change -> {
            while (change.next()) {
                // Schedule reminder -> Activation appears on dashboard
                if (change.wasAdded()) {
                    for (int i = 0; i < change.getAddedSize(); i++) {
                        ActivationModel addedItem = change.getAddedSubList().get(i);
                        Node view = createActivationView(addedItem);
                        scheduleContainer.getChildren().add(change.getFrom() + i, view);
                    }
                }

                // Reminder alert appears and Activation is dismissed -> Activation disappears from dashboard
                if (change.wasRemoved()) {
                    scheduleContainer.getChildren().remove(
                            change.getFrom(),
                            change.getFrom() + change.getRemovedSize()
                    );
                }

                // Activation time is altered -> Dashboard is resorted so Activations appear in chronological order
                if (change.wasPermutated() || change.wasUpdated()) {
                    FXCollections.sort(scheduleContainer.getChildren(),
                            Comparator.comparingInt(node -> activations.indexOf((ActivationModel) node.getUserData())));
                }
            }
        });
    }
}
