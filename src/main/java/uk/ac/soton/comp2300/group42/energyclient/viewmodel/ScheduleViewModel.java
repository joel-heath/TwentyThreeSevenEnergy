package uk.ac.soton.comp2300.group42.energyclient.viewmodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import uk.ac.soton.comp2300.group42.energyclient.model.entity.Activation;
import uk.ac.soton.comp2300.group42.energyclient.model.entity.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.model.repository.ActivationRepository;
import uk.ac.soton.comp2300.group42.energyclient.model.repository.ApplianceRepository;
import uk.ac.soton.comp2300.group42.energyclient.services.NotificationService;
import java.time.LocalDateTime;

public class ScheduleViewModel {

    private final ObservableList<Appliance> applianceList = FXCollections.observableArrayList();
    private final ObjectProperty<Appliance> selectedAppliance = new SimpleObjectProperty<>();
    private final ApplianceRepository applianceRepository;
    private final ActivationRepository activationRepository;
    private final NotificationService notificationService;

    public ScheduleViewModel(ApplianceRepository applianceRepo,  ActivationRepository activationRepo, NotificationService notificationService) {
        this.applianceRepository = applianceRepo;
        this.activationRepository = activationRepo;
        this.notificationService = notificationService;
        loadAppliances();
    }

    private void loadAppliances() {
        var data = applianceRepository.findAll();
        applianceList.addAll(data);
    }

    public ObservableList<Appliance> getApplianceList() { return applianceList; }

    public ObjectProperty<Appliance> selectedApplianceProperty() { return selectedAppliance; }

    public Appliance getSelectedAppliance() { return selectedAppliance.get(); }

    public void scheduleActivation(LocalDateTime targetDateTime) {
        Appliance selected = selectedAppliance.get();
        Activation activation = new Activation(-1, selected, targetDateTime);

        activationRepository.save(activation);
        notificationService.scheduleNotification(activation);
    }


}