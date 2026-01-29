package uk.ac.soton.comp2300.group42.energyclient.controller;

import javafx.collections.ListChangeListener;
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

    double maxBarWidth = 300;

    private final DashboardViewModel vm;

    public DashboardController(DashboardViewModel vm) { this.vm = vm; }

    @FXML private void initialize() {
        counterLabel.textProperty().bind(vm.counterProperty().asString());
        costLabel.textProperty().bind(vm.costProperty());
        usageRect.widthProperty().bind(vm.usageProperty().multiply(maxBarWidth));
        goalLabel.textProperty().bind(vm.goalProperty());

        vm.getActivations().addListener((ListChangeListener<Activation>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Activation a : change.getAddedSubList()) {
                        Pane cardNode = createActivationView(a);
                        scheduleContainer.getChildren().add(cardNode);
                    }
                }
            }
        });

        vm.loadCards();
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

    @FXML private HBox scheduleContainer;

    private Pane createActivationView(Activation activation) {
        VBox card = new VBox();
        // card.setPrefSize(100, 150);
        card.setStyle("-fx-background-color: lightblue; -fx-background-radius: 5; -fx-padding: 5;");

        card.getChildren().addAll(
                new Label(activation.getAppliance().getName()),
                new Label(activation.getActivationTime().format(DateTimeFormatter.ofPattern("HH:mm")))
        );
        return card;
    }
}
