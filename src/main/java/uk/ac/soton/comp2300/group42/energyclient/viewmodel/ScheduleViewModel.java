package uk.ac.soton.comp2300.group42.energyclient.viewmodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import uk.ac.soton.comp2300.group42.energyclient.model.entity.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.model.repository.ApplianceRepository;

public class ScheduleViewModel {

    private final ObservableList<Appliance> applianceList = FXCollections.observableArrayList();
    private final ObjectProperty<Appliance> selectedAppliance = new SimpleObjectProperty<>();
    private final ApplianceRepository repository;

    public ScheduleViewModel() {
        this(new ApplianceRepository());
    }

    public ScheduleViewModel(ApplianceRepository repository) {
        this.repository = repository;
        loadAppliances();
    }

    private void loadAppliances() {
        var data = repository.findAll();
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
}