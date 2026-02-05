package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import javafx.collections.ObservableList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import uk.ac.soton.comp2300.group42.energyclient.data.dto.ActivationDTO;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ApplianceModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Repository;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public class ScheduleViewModel {

    private final Repository repository;
    private final ObservableList<ApplianceModel> applianceList;
    private final ObjectProperty<ApplianceModel> selectedAppliance;

    public ScheduleViewModel(Repository repository) {
        this.repository = repository;
        this.applianceList = repository.getAppliances();
        selectedAppliance = new SimpleObjectProperty<>();
        CompletableFuture.runAsync(repository::fetchAllData);
    }

    public ObservableList<ApplianceModel> getApplianceList() { return applianceList; }
    public ApplianceModel getSelectedAppliance() { return selectedAppliance.get(); }
    public ObjectProperty<ApplianceModel> selectedApplianceProperty() { return selectedAppliance; }

    public void scheduleActivation(LocalDateTime targetDateTime) {
        ApplianceModel selected = selectedAppliance.get();
        if (selected == null)
            return;
        ActivationDTO dto = new ActivationDTO(selected.getId(), targetDateTime);
        repository.createActivation(dto);
    }
}