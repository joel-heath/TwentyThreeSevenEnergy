package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import uk.ac.soton.comp2300.group42.energyclient.data.api.EnergyParser;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.EnergyPriceModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.services.EnergyPriceService;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.ui.view.components.ActivationSchedulePane;
import uk.ac.soton.comp2300.group42.energyclient.ui.view.components.EnergyUsageWidget;
import uk.ac.soton.comp2300.group42.energyclient.ui.view.components.Modal;
import uk.ac.soton.comp2300.group42.energyclient.ui.view.components.ScheduleApplianceWidget;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.EnergyUsageWidgetViewModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.ScheduleApplianceWidgetViewModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.SimpleDashboardViewModel;

public class SimpleDashboardController {

    @FXML private VBox energyWidgetContainer;
    @FXML private VBox scheduleApplianceWidgetContainer;
    @FXML private Modal editModal;
    @FXML private ActivationSchedulePane schedulePane;
    @FXML private Label responseLabel;
    @FXML private Label priceLabel;

    private final EnergyPriceService service = new EnergyPriceService();

    private final SimpleDashboardViewModel vm;
    private ScheduleApplianceWidget scheduleApplianceWidget;

    public SimpleDashboardController(SimpleDashboardViewModel vm) { this.vm = vm; }

    @FXML private void initialize() {
        EnergyUsageWidgetViewModel widgetVm = new EnergyUsageWidgetViewModel(vm.getPreferences());
        EnergyUsageWidget widget = new EnergyUsageWidget(widgetVm);
        energyWidgetContainer.getChildren().add(widget);

        ScheduleApplianceWidgetViewModel scheduleApplianceWidgetVm = new ScheduleApplianceWidgetViewModel(vm.getRepository());
        scheduleApplianceWidget = new ScheduleApplianceWidget(scheduleApplianceWidgetVm, editModal, schedulePane, responseLabel);
        scheduleApplianceWidgetContainer.getChildren().add(scheduleApplianceWidget);

        loadPrice();

        vm.startAutoUpdateTest();
    }

    @FXML private void onCloseEditModal() {
        scheduleApplianceWidget.onCloseEditModal();
    }

    @FXML private void onSaveActivation() {
        scheduleApplianceWidget.onSaveActivation();
    }

    @FXML private void onCancelActivation() {
        scheduleApplianceWidget.onCancelActivation();
    }

    @FXML private void onSchedule() {
        Navigator.goTo("Schedule.fxml");
    }

    @FXML private void onManageHouses() {
        Navigator.goTo("ManageHouses.fxml");
    }

    private void loadPrice() {
        new Thread(() -> {
            try {
                String json = service.fetchRawData();
                EnergyPriceModel price = EnergyParser.parse(json);

                Platform.runLater(() ->
                        priceLabel.setText(
                                String.format("%.2f p/kWh", price.getPrice())
                        )
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
