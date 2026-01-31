package uk.ac.soton.comp2300.group42.energyclient.controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import uk.ac.soton.comp2300.group42.energyclient.model.entity.Activation;
import uk.ac.soton.comp2300.group42.energyclient.viewmodel.DashboardViewModel;
import uk.ac.soton.comp2300.group42.energyclient.util.Navigator;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import java.time.format.DateTimeFormatter;


public class DashboardController {

    @FXML private Label counterLabel;
    @FXML private Label costLabel;
    @FXML private Label goalLabel;

    @FXML private TextField costGoalField;
    @FXML private Rectangle usageRect;

    @FXML private HBox scheduleContainer;

    private static final double maxBarWidth = 300;

    private final DashboardViewModel vm;

    public DashboardController(DashboardViewModel vm) { this.vm = vm; }

    @FXML private void initialize() {
        counterLabel.textProperty().bind(vm.counterProperty().asString());
        costLabel.textProperty().bind(vm.costProperty());
        usageRect.widthProperty().bind(vm.usageProperty().multiply(maxBarWidth));
        goalLabel.textProperty().bind(vm.goalProperty());

        bindActivations();
    }

    private void bindActivations() {
        // `activations` is the live list, the ViewModel passes it on from the ActivationRepository
        SortedList<Activation> activations = vm.getActivations();

        scheduleContainer.getChildren().clear();
        for (Activation activation : activations) {
            scheduleContainer.getChildren().add(createActivationView(activation));
        }

        activations.addListener((ListChangeListener<Activation>) change -> {
            while (change.next()) {
                // Schedule reminder -> Activation appears on dashboard
                if (change.wasAdded()) {
                    for (int i = 0; i < change.getAddedSize(); i++) {
                        Activation addedItem = change.getAddedSubList().get(i);
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

    private void sortActivations(SortedList<Activation> activations) {
        FXCollections.sort(scheduleContainer.getChildren(), (node1, node2) -> {
            Activation a1 = (Activation) node1.getUserData();
            Activation a2 = (Activation) node2.getUserData();

            int index1 = activations.indexOf(a1);
            int index2 = activations.indexOf(a2);

            return Integer.compare(index1, index2);
        });
    }

    public void onSchedule() {
        Navigator.goTo("schedule.fxml");
    }

    public void onIncrement() {
        vm.incrementCounter();
    }

    public void onRecalculate() {
        vm.recalculateCost();
    }

    public void onSetCostGoal() {
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

    private Pane createActivationView(Activation activation) {
        VBox card = new VBox();
        // card.setPrefSize(100, 150);
        card.setStyle("-fx-background-color: lightblue; -fx-background-radius: 5; -fx-padding: 5;");

        Button removeButton = new Button("Remove");
        removeButton.setOnAction((_) -> vm.removeActivation(activation));

        card.getChildren().addAll(
                new Label(activation.getAppliance().getName()),
                new Label(activation.getActivationTime().format(DateTimeFormatter.ofPattern("HH:mm"))),
                removeButton
        );

        card.setUserData(activation); // for sorting when an activation's time is changed.

        return card;
    }
}
