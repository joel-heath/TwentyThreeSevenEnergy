package uk.ac.soton.comp2300.group42.energyclient.ui.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.ActivationDTO;

import java.time.LocalDateTime;

public class ActivationModel {
    private final ObjectProperty<ApplianceModel> appliance;
    private final ObjectProperty<LocalDateTime> activationTime;
    private final ActivationDTO dto;

    public ActivationModel(ActivationDTO dto, ApplianceModel appliance) {
        this.dto = dto;
        this.appliance = new SimpleObjectProperty<>(appliance);
        this.activationTime = new SimpleObjectProperty<>(dto.getActivationTime());
    }

    public ActivationDTO commit() {
        // dto.setAppliance(appliance.get().commit()); not necessary with the model factory
        dto.setActivationTime(activationTime.get());

        return dto;
    }

    public Long getId() { return dto.getId(); }

    public ApplianceModel getAppliance() { return appliance.get(); }
    public void setAppliance(ApplianceModel appliance) { this.appliance.set(appliance); }
    public ObjectProperty<ApplianceModel> applianceProperty() { return appliance; }

    public LocalDateTime getActivationTime() { return activationTime.get(); }
    public void setActivationTime(LocalDateTime activationTime) { this.activationTime.set(activationTime); }
    public ObjectProperty<LocalDateTime> activationTimeProperty() { return activationTime; }
}
