package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import com.google.inject.Inject;
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
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.SharedDashboardViewModel;

public class SimpleDashboardController {

    @FXML private EnergyUsageWidget energyWidget;
    @FXML private VBox scheduleApplianceWidgetContainer;
    @FXML private Modal editModal;
    @FXML private ActivationSchedulePane schedulePane;
    @FXML private Label responseLabel;
    @FXML private Label priceLabel;

    private final EnergyPriceService service = new EnergyPriceService();

    private final SharedDashboardViewModel vm;
    private final EnergyUsageWidgetViewModel energyWidgetVM;
    private ScheduleApplianceWidget scheduleApplianceWidget;

    @Inject public SimpleDashboardController(SharedDashboardViewModel vm,
                                             EnergyUsageWidgetViewModel energyWidgetVM) {
        this.vm = vm;
        this.energyWidgetVM = energyWidgetVM;
    }

    @FXML private void initialize() {
        energyWidget.bindComponents(energyWidgetVM);
        energyWidgetVM.startAutoUpdateTest();

        ScheduleApplianceWidgetViewModel scheduleApplianceWidgetVm = new ScheduleApplianceWidgetViewModel(vm.getRepository());
        scheduleApplianceWidget = new ScheduleApplianceWidget(scheduleApplianceWidgetVm, editModal, schedulePane, responseLabel);
        scheduleApplianceWidgetContainer.getChildren().add(scheduleApplianceWidget);

        loadPrice();
    }

    @FXML private void onCloseEditModal() {
        scheduleApplianceWidget.onCloseEditModal();
    }

    @FXML private void onSaveActivation() {
        scheduleApplianceWidget.onSaveActivation();
    }

    public void onCancelActivation() {
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
