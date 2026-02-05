package uk.ac.soton.comp2300.group42.energyclient.data.dto;

import java.time.LocalDateTime;

public class ActivationDTO {
    private final Long id;
    private Long applianceId;
    private LocalDateTime activationTime;

    public ActivationDTO(Long applianceId, LocalDateTime activationTime) {
        this.id = null;
        this.applianceId = applianceId;
        this.activationTime = activationTime;
    }

    public Long getId() { return id; }

    public Long getApplianceId() { return applianceId; }
    public void setApplianceId(Long applianceId) { this.applianceId = applianceId; }

    public LocalDateTime getActivationTime() { return activationTime; }
    public void setActivationTime(LocalDateTime activationTime) { this.activationTime = activationTime; }
}
