package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import com.google.inject.Inject;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.UnitRate;
import uk.ac.soton.comp2300.group42.energyclient.ui.view.components.*;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.AdvancedDashboardViewModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.EnergyUsageViewModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.UpcomingActivationsViewModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Navigator;

public class AdvancedDashboardController {

    @FXML private EnergyUsageWidget energyWidget;
    @FXML private UpcomingActivationsWidget upcomingActivationsWidget;
    @FXML private ActivationEditModal activationEditModal;

    @FXML private HBox statusRow;

    private final AdvancedDashboardViewModel vm = new AdvancedDashboardViewModel();

    private final EnergyUsageViewModel energyWidgetVM;
    private final UpcomingActivationsViewModel activationsWidgetVM;

    @Inject public AdvancedDashboardController(EnergyUsageViewModel energyWidgetVM,
                                               UpcomingActivationsViewModel activationsWidgetVM) {
        this.energyWidgetVM = energyWidgetVM;
        this.activationsWidgetVM = activationsWidgetVM;
    }

    @FXML private void initialize() {
        energyWidget.bindComponents(energyWidgetVM);
        energyWidgetVM.startAutoUpdateTest();

        upcomingActivationsWidget.bindComponents(activationsWidgetVM, activationEditModal);

        vm.getHourlyForecast().addListener((ListChangeListener<UnitRate>) c -> {
            statusRow.getChildren().clear();
            for (UnitRate rate : vm.getHourlyForecast()) {
                statusRow.getChildren().add(createStatusCard(rate));
            }
        });
        vm.loadDashboardData();
    }

    @FXML private void onManageHouses() {
        Navigator.goTo("ManageHouses.fxml");
    }

    @FXML private void onProgressTracking() {
        Navigator.goTo("ProgressTracking.fxml");
    }

    private VBox createStatusCard(UnitRate rate) {
        VBox card = new VBox(0);
        card.setStyle("-fx-alignment: center; -fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5;");
        card.setPrefHeight(40);

        card.setPadding(new Insets(2, 5, 2, 5));

        Label emojiLabel = new Label();
        String status = rate.getPriceStatus();

        if (status.equals("CHEAP")) {
            emojiLabel.setText("😊");
            card.setStyle(card.getStyle() + "-fx-background-color: #d4edda;"); // Green - change colours according to colourblind mode
        } else if (status.equals("AVERAGE")) {
            emojiLabel.setText("😐");
            card.setStyle(card.getStyle() + "-fx-background-color: #fff3cd;"); // Yellow
        } else {
            emojiLabel.setText("⚠️");
            card.setStyle(card.getStyle() + "-fx-background-color: #f8d7da;"); // Red
        }

        String time = rate.validFrom().substring(11, 16);
        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-font-size: 9px;");

        card.getChildren().addAll(emojiLabel, timeLabel);
        return card;
    }
}
