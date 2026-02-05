package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import uk.ac.soton.comp2300.group42.energyclient.ui.model.ActivationModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ApplianceModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ColourSettings;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.ui.view.components.ActivationSchedulePane;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.DashboardViewModel;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DashboardController {

    @FXML private VBox mainContentArea;
    @FXML private StackPane editModalOverlay;
    @FXML private ActivationSchedulePane schedulePane;
    private ActivationModel currentEditingActivation;
    private final BoxBlur blur = new BoxBlur(10, 10, 3);

    @FXML private Label costLabel;
    @FXML private Label goalLabel;

    @FXML private TextField costGoalField;
    @FXML private Rectangle usageRect;
    private final Rectangle clip = new Rectangle();

    @FXML private HBox scheduleContainer;

    private static final double maxBarWidth = 250;

    private final DashboardViewModel vm;

    public DashboardController(DashboardViewModel vm) { this.vm = vm; }

    @FXML private void initialize() {
        costLabel.textProperty().bind(vm.costMessageProperty());
        clip.widthProperty().bind(vm.usageProperty().multiply(maxBarWidth));
        goalLabel.textProperty().bind(vm.goalMessageProperty());
        bindActivations();

        clip.setHeight(25);
        usageRect.setClip(clip);
        usageRect.fillProperty().bind(ColourSettings.usageGradientProperty());

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

        mainContentArea.setEffect(blur);
        editModalOverlay.setVisible(true);
    }

    @FXML private void onCloseEditModal() {
        mainContentArea.setEffect(null);
        editModalOverlay.setVisible(false);
        this.currentEditingActivation = null;
    }

    @FXML private void onSaveActivation() {
        ApplianceModel appliance = schedulePane.getSelectedAppliance();
        int hour = schedulePane.getHour();
        int minute = schedulePane.getMinute();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetDateTime = LocalDateTime.of(now.toLocalDate(), LocalTime.of(hour, minute));

        boolean isBeforeOrNow = targetDateTime.isBefore(now) || targetDateTime.isEqual(now);
        if (isBeforeOrNow) targetDateTime = targetDateTime.plusDays(1);

        vm.updateActivation(currentEditingActivation, appliance, targetDateTime);
        onCloseEditModal();
    }

    @FXML private void onCancelActivation() {
        vm.removeActivation(currentEditingActivation);
        onCloseEditModal();
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
                if (change.wasPermutated() || change.wasUpdated())
                    sortActivations(activations);
            }
        });
    }

    private void sortActivations(SortedList<ActivationModel> activations) {
        FXCollections.sort(scheduleContainer.getChildren(), (node1, node2) -> {
            ActivationModel a1 = (ActivationModel) node1.getUserData();
            ActivationModel a2 = (ActivationModel) node2.getUserData();

            int index1 = activations.indexOf(a1);
            int index2 = activations.indexOf(a2);

            return Integer.compare(index1, index2);
        });
    }

    @FXML private void onSchedule() {
        Navigator.goTo("schedule.fxml");
    }

    @FXML private void onSetCostGoal() {
        try {
            double value = Double.parseDouble(costGoalField.getText());

            if (value <= 0) throw new NumberFormatException();

            vm.setCostGoal(value);
            costGoalField.clear();
            costGoalField.setStyle("");

        } catch (NumberFormatException e) {
            costGoalField.setStyle("-fx-border-color: red;");
        }
    }

    private Pane createActivationView(ActivationModel activation) {
        VBox card = new VBox();
        // card.setPrefSize(100, 150);
        card.setStyle("-fx-background-color: lightblue; -fx-background-radius: 5; -fx-padding: 5; -fx-spacing: 5");

        Label nameLabel = new Label();
        Label timeLabel = new Label();

        nameLabel.textProperty().bind(
                activation.applianceProperty().flatMap(ApplianceModel::nameProperty)
        );
        timeLabel.textProperty().bind(Bindings.createStringBinding(
                () -> activation.getActivationTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                activation.activationTimeProperty()
        ));

        card.getChildren().addAll(nameLabel, timeLabel);

        card.setOnMouseClicked(_ -> this.openEditModal(activation));
        card.setUserData(activation); // for sorting when an activation's time is changed.

        return card;
    }
}
