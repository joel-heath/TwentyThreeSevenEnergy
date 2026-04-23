package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.ActivationEditModal;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.CurrentWeatherWidget;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.EnergyUsageWidget;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.UpcomingActivationsWidget;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.*;

public class AdvancedDashboardController {

    @FXML private EnergyUsageWidget energyWidget;
    @FXML private CurrentWeatherWidget currentWeatherWidget;
    @FXML private UpcomingActivationsWidget upcomingActivationsWidget;
    @FXML private ActivationEditModal activationEditModal;
    @FXML private HBox statusRow;

    private final AdvancedDashboardViewModel vm;
    private final EnergyUsageViewModel energyWidgetVM;
    private final CurrentWeatherViewModel weatherVM;
    private final UpcomingActivationsViewModel activationsWidgetVM;
    private final ActivationEditViewModel editModalVM;

    @Inject public AdvancedDashboardController(AdvancedDashboardViewModel vm,
                                               EnergyUsageViewModel energyWidgetVM,
                                               CurrentWeatherViewModel weatherVM,
                                               UpcomingActivationsViewModel activationsWidgetVM,
                                               ActivationEditViewModel editModalVM) {
        this.vm = vm;
        this.energyWidgetVM = energyWidgetVM;
        this.weatherVM = weatherVM;
        this.activationsWidgetVM = activationsWidgetVM;
        this.editModalVM = editModalVM;
    }

    @FXML private void initialize() {
        energyWidget.bindComponents(energyWidgetVM);
        currentWeatherWidget.bindComponents(weatherVM);

        upcomingActivationsWidget.bindComponents(activationsWidgetVM, editModalVM, activationEditModal);

        vm.getHourlyForecast().subscribe(() -> {
            statusRow.getChildren().clear();
            for (AdvancedDashboardViewModel.StatusCardState state : vm.getHourlyForecast()) {
                statusRow.getChildren().add(createStatusCard(state));
            }
        });

        upcomingActivationsWidget.loadActivationsAsync();
        vm.loadDashboardData();
    }

    @FXML private void onManageHouses() {
        Navigator.goTo("ManageHouses.fxml");
    }

    @FXML private void onProgressTracking() {
        Navigator.goTo("ProgressTracking.fxml");
    }

    private VBox createStatusCard(AdvancedDashboardViewModel.StatusCardState state) {
        VBox card = new VBox(0);
        card.setPrefHeight(40);
        card.getStyleClass().addAll("status-card", state.statusStyleClass());

        Label statusLabel = new Label(state.emoji());
        statusLabel.setStyle("-fx-text-fill: -fx-app-status-row-text;");

        Label timeLabel = new Label(state.timeText());
        timeLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: -fx-app-status-row-text;");

        card.getChildren().addAll(statusLabel, timeLabel);
        return card;
    }
}
