package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.ColorVisionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.ActivationEditModal;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.EnergyUsageWidget;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.UpcomingActivationsWidget;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.ActivationEditViewModel;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.AdvancedDashboardViewModel;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.EnergyUsageViewModel;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.UpcomingActivationsViewModel;

public class AdvancedDashboardController {

    private static final String STATUS_CARD_BASE_STYLE =
            "-fx-alignment: center; -fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5;";

    @FXML private EnergyUsageWidget energyWidget;
    @FXML private UpcomingActivationsWidget upcomingActivationsWidget;
    @FXML private ActivationEditModal activationEditModal;
    @FXML private HBox statusRow;

    private final AdvancedDashboardViewModel vm;
    private final EnergyUsageViewModel energyWidgetVM;
    private final UpcomingActivationsViewModel activationsWidgetVM;
    private final ActivationEditViewModel editModalVM;

    @Inject public AdvancedDashboardController(AdvancedDashboardViewModel vm,
                                               EnergyUsageViewModel energyWidgetVM,
                                               UpcomingActivationsViewModel activationsWidgetVM,
                                               ActivationEditViewModel editModalVM) {
        this.vm = vm;
        this.energyWidgetVM = energyWidgetVM;
        this.activationsWidgetVM = activationsWidgetVM;
        this.editModalVM = editModalVM;
    }

    @FXML private void initialize() {
        energyWidget.bindComponents(energyWidgetVM);

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
        card.setPadding(new Insets(2, 5, 2, 5));

        Label statusLabel = new Label(state.emoji());

        Label timeLabel = new Label(state.timeText());
        timeLabel.setStyle("-fx-font-size: 9px;");

        card.styleProperty().bind(ColorVisionManager.visionProperty().map(
                vision -> STATUS_CARD_BASE_STYLE + "-fx-background-color: " +
                        ColorVisionManager.getWebColor(vision, state.colorRole()) + ";"
        ));

        card.getChildren().addAll(statusLabel, timeLabel);
        return card;
    }
}
