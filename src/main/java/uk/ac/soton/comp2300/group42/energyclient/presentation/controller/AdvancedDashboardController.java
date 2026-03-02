package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.PriceStatus;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.UnitRate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.ColorVisionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.ActivationEditModal;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.EnergyUsageWidget;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.UpcomingActivationsWidget;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.AdvancedDashboardViewModel;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.EnergyUsageViewModel;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.UpcomingActivationsViewModel;

import java.time.format.DateTimeFormatter;

public class AdvancedDashboardController {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final String STATUS_CARD_BASE_STYLE =
            "-fx-alignment: center; -fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5;";

    @FXML private EnergyUsageWidget energyWidget;
    @FXML private UpcomingActivationsWidget upcomingActivationsWidget;
    @FXML private ActivationEditModal activationEditModal;

    @FXML private HBox statusRow;

    private final AdvancedDashboardViewModel vm;
    private final EnergyUsageViewModel energyWidgetVM;
    private final UpcomingActivationsViewModel activationsWidgetVM;

    @Inject public AdvancedDashboardController(AdvancedDashboardViewModel vm,
                                               EnergyUsageViewModel energyWidgetVM,
                                               UpcomingActivationsViewModel activationsWidgetVM) {
        this.vm = vm;
        this.energyWidgetVM = energyWidgetVM;
        this.activationsWidgetVM = activationsWidgetVM;
    }

    @FXML private void initialize() {
        energyWidget.bindComponents(energyWidgetVM);
        energyWidgetVM.startAutoUpdateTest();

        upcomingActivationsWidget.bindComponents(activationsWidgetVM, activationEditModal);

        vm.getHourlyForecast().addListener((ListChangeListener<UnitRate>) _ -> {
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
        card.setPrefHeight(40);
        card.setPadding(new Insets(2, 5, 2, 5));

        Label statusLabel = new Label();
        PriceStatus status = rate.getPriceStatus();
        ColorVisionManager.ColorRole colorRole;

        if (status == PriceStatus.CHEAP) {
            statusLabel.setText("\uD83D\uDE0A");
            colorRole = ColorVisionManager.ColorRole.STATUS_CHEAP;
        }
        else if (status == PriceStatus.AVERAGE) {
            statusLabel.setText("\uD83D\uDE10");
            colorRole = ColorVisionManager.ColorRole.STATUS_AVERAGE;
        }
        else {
            statusLabel.setText("\uD83D\uDE41");
            colorRole = ColorVisionManager.ColorRole.STATUS_EXPENSIVE;
        }

        card.styleProperty().bind(ColorVisionManager.visionProperty().map(
                vision -> STATUS_CARD_BASE_STYLE + "-fx-background-color: " + ColorVisionManager.getWebColor(vision, colorRole) + ";"
        ));

        Label timeLabel = new Label(rate.validFrom().format(TIME_FORMATTER));
        timeLabel.setStyle("-fx-font-size: 9px;");

        card.getChildren().addAll(statusLabel, timeLabel);
        return card;
    }
}
