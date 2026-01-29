package uk.ac.soton.comp2300.group42.energyclient.model.entity;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDateTime;

public class Activation {
    private int id;
    private Appliance appliance;
    private final ObjectProperty<LocalDateTime> activationTime;

    public Activation(int id, Appliance appliance, LocalDateTime activationTime) {
        this.id = id;
        this.appliance = appliance;
        this.activationTime = new SimpleObjectProperty<>(activationTime);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Appliance getAppliance() { return appliance; }
    public void setAppliance(Appliance appliance) { this.appliance = appliance; }

    public ObjectProperty<LocalDateTime> activationTimeProperty() { return activationTime; }
    public LocalDateTime getActivationTime() { return activationTime.get(); }
    public void setActivationTime(LocalDateTime activationTime) { this.activationTime.set(activationTime); }
}
