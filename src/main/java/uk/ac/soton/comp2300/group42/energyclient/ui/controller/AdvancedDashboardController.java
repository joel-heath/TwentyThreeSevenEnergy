package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.energyclient.ui.view.components.ActivationSchedulePane;
import uk.ac.soton.comp2300.group42.energyclient.ui.view.components.EnergyUsageWidget;
import uk.ac.soton.comp2300.group42.energyclient.ui.view.components.ScheduleApplianceWidget;
import uk.ac.soton.comp2300.group42.energyclient.ui.view.components.Modal;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.AdvancedDashboardViewModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.EnergyUsageWidgetViewModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.ScheduleApplianceWidgetViewModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Navigator;

import javafx.scene.control.Label;


public class AdvancedDashboardController {
    @FXML private VBox energyWidgetContainer;
    @FXML private VBox scheduleApplianceWidgetContainer;
    @FXML private Modal editModal;
    @FXML private ActivationSchedulePane schedulePane;
    @FXML private Label responseLabel;

    private final AdvancedDashboardViewModel vm;
    private ScheduleApplianceWidget scheduleApplianceWidget;

    public AdvancedDashboardController(AdvancedDashboardViewModel vm) {
        this.vm = vm;
    }

    @FXML private void initialize() {
        EnergyUsageWidgetViewModel widgetVm =
                new EnergyUsageWidgetViewModel(vm.getRepository().getPreferences());
        EnergyUsageWidget widget = new EnergyUsageWidget(widgetVm);
        energyWidgetContainer.getChildren().add(widget);

        ScheduleApplianceWidgetViewModel scheduleApplianceWidgetVm =
                new ScheduleApplianceWidgetViewModel(vm.getRepository());
        ScheduleApplianceWidget scheduleApplianceWidget = new ScheduleApplianceWidget(scheduleApplianceWidgetVm, editModal, schedulePane, responseLabel);
        scheduleApplianceWidgetContainer.getChildren().add(scheduleApplianceWidget);
    }

    @FXML private void onSaveActivation() {
        scheduleApplianceWidget.onSaveActivation();
    }

    @FXML private void onCancelActivation() {
        scheduleApplianceWidget.onCancelActivation();
    }

    @FXML private void onCloseEditModal() {
        scheduleApplianceWidget.onCloseEditModal();
    }

    @FXML private void onManageHouses() {
        Navigator.goTo("ManageHouses.fxml");
    }

    @FXML private void onProgressTracking() {}
}
