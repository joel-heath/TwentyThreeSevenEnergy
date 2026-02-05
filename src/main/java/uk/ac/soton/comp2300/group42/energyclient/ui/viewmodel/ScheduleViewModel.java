package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import uk.ac.soton.comp2300.group42.energyclient.data.dto.ActivationDTO;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.ApplianceDTO;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ActivationClient;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ApplianceClient;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ActivationModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ApplianceModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.services.NotificationService;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.ModelFactory;

import java.time.LocalDateTime;

public class ScheduleViewModel {

    private final ObservableList<ApplianceModel> applianceList = FXCollections.observableArrayList();
    private final ObjectProperty<ApplianceModel> selectedAppliance = new SimpleObjectProperty<>();

    private final ModelFactory modelFactory;
    private final ApplianceClient applianceClient;
    private final ActivationClient activationClient;
    private final NotificationService notificationService;

    public ScheduleViewModel(ModelFactory modelFactory, ApplianceClient applianceRepo,  ActivationClient activationRepo, NotificationService notificationService) {
        this.modelFactory = modelFactory;
        this.applianceClient = applianceRepo;
        this.activationClient = activationRepo;
        this.notificationService = notificationService;
        loadAppliances();
    }

    private void loadAppliances() {
        var dtos = applianceClient.findAll();
        var models = dtos.stream().map(modelFactory::getApplianceModel).toList();
        applianceList.addAll(models);
    }

    public ObservableList<ApplianceModel> getApplianceList() { return applianceList; }
    public ApplianceModel getSelectedAppliance() { return selectedAppliance.get(); }
    public ObjectProperty<ApplianceModel> selectedApplianceProperty() { return selectedAppliance; }

    public void scheduleActivation(LocalDateTime targetDateTime) {
        ApplianceModel selected = selectedAppliance.get();
        ApplianceDTO selectedDTO = selected.commit();
        ActivationModel activation = modelFactory.createActivationModel(
                new ActivationDTO(selectedDTO, targetDateTime)
        );
        ActivationDTO activationDTO = activation.commit();

        activationClient.save(activationDTO);
        notificationService.scheduleNotification(activation);
    }
}