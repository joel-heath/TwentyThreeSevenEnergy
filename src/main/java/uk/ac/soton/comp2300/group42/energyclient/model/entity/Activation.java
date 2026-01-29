package uk.ac.soton.comp2300.group42.energyclient.model.entity;

import java.time.LocalDateTime;

public class Activation {
    private int id;
    private Appliance appliance;
    private LocalDateTime activationTime;

    public Activation(int id, Appliance appliance, LocalDateTime activationTime) {
        this.id = id;
        this.appliance = appliance;
        this.activationTime = activationTime;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Appliance getAppliance() { return appliance; }
    public void setAppliance(Appliance appliance) { this.appliance = appliance; }

    public LocalDateTime getActivationTime() { return activationTime; }
    public void setActivationTime(LocalDateTime activationTime) { this.activationTime = activationTime; }
}
