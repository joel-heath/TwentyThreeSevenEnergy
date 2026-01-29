package uk.ac.soton.comp2300.group42.energyclient.viewmodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import uk.ac.soton.comp2300.group42.energyclient.model.entity.Activation;
import uk.ac.soton.comp2300.group42.energyclient.model.entity.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.model.repository.ActivationRepository;
import uk.ac.soton.comp2300.group42.energyclient.model.repository.ApplianceRepository;

public class ScheduleViewModel {

    private final ObservableList<Appliance> applianceList = FXCollections.observableArrayList();
    private final ObjectProperty<Appliance> selectedAppliance = new SimpleObjectProperty<>();
    private final ApplianceRepository applianceRepository;
    private final ActivationRepository activationRepository;

    public ScheduleViewModel(ApplianceRepository applianceRepo,  ActivationRepository activationRepo) {
        this.applianceRepository = applianceRepo;
        this.activationRepository = activationRepo;
        loadAppliances();
    }

    private void loadAppliances() {
        var data = applianceRepository.findAll();
        applianceList.addAll(data);
    }

    public ObservableList<Appliance> getApplianceList() {
        return applianceList;
    }

    public ObjectProperty<Appliance> selectedApplianceProperty() {
        return selectedAppliance;
    }

    public Appliance getSelectedAppliance() {
        return selectedAppliance.get();
    }

    public void scheduleAppliance(Activation activation) {
        activationRepository.save(activation);
    }
}