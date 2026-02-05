package uk.ac.soton.comp2300.group42.energyclient.ui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.ApplianceDTO;

public class ApplianceModel {
    private final ApplianceDTO dto;
    private final StringProperty name;

    public ApplianceModel(ApplianceDTO dto) {
        this.dto = dto;
        this.name = new SimpleStringProperty(dto.getName());
    }

    public ApplianceDTO commit() {
        dto.setName(name.get());
        return dto;
    }

    public Long getId() { return dto.getId(); }

    public String getName() { return name.get(); }
    public void setName(String value) { name.set(value); }
    public StringProperty nameProperty() { return name; }

    @Override public String toString() { return name.get(); }
}
