package uk.ac.soton.comp2300.group42.energyclient.controller;


import javafx.scene.control.TextField;
import javafx.scene.shape.Rectangle;
import uk.ac.soton.comp2300.group42.energyclient.viewmodel.DashboardViewModel;
import uk.ac.soton.comp2300.group42.energyclient.util.Navigator;
import javafx.scene.control.Label;
import javafx.fxml.FXML;


public class DashboardController {

    @FXML private Label counterLabel;
    @FXML private Label costLabel;
    @FXML private Label goalLabel;

    private final DashboardViewModel vm = new DashboardViewModel();

    @FXML private TextField costGoalField;
    @FXML private Rectangle usageRect;

    double maxBarWidth = 300;

    @FXML
    private void initialize() {
        counterLabel.textProperty().bind(vm.counterProperty().asString());
        costLabel.textProperty().bind(vm.costProperty());
        usageRect.widthProperty().bind(vm.usageProperty().multiply(maxBarWidth));
        goalLabel.textProperty().bind(vm.goalProperty());
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
}
