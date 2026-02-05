package uk.ac.soton.comp2300.group42.energyclient.data.dto;

import java.time.LocalDateTime;

public class ActivationDTO {
    private final Long id;
    private ApplianceDTO appliance;
    private LocalDateTime activationTime;

    public ActivationDTO(ApplianceDTO appliance, LocalDateTime activationTime) {
        this.id = null;
        this.appliance = appliance;
        this.activationTime = activationTime;
    }

    public Long getId() { return id; }

    public ApplianceDTO getAppliance() { return appliance; }
    public void setAppliance(ApplianceDTO appliance) { this.appliance = appliance; }

    public LocalDateTime getActivationTime() { return activationTime; }
    public void setActivationTime(LocalDateTime activationTime) { this.activationTime = activationTime; }
}
