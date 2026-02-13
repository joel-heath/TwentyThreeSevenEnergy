package uk.ac.soton.comp2300.group42.energyclient.ui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.HouseDTO;

import static uk.ac.soton.comp2300.group42.energyclient.ui.util.ModelUtils.updateIfChanged;

public class HouseModel {

    private final HouseDTO dto;
    private final StringProperty name;
    private final StringProperty address;

    public HouseModel(HouseDTO dto) {
        this.dto = dto;
        this.name = new SimpleStringProperty(dto.getName());
        this.address = new SimpleStringProperty(dto.getAddress());
    }

    public HouseDTO commit() {
        dto.setName(name.get());
        dto.setAddress(address.get());
        return dto;
    }

    public void updateFrom(HouseDTO dto) {
        updateIfChanged(getName(), dto.getName(), this::setName);
        updateIfChanged(getAddress(), dto.getAddress(), this::setAddress);
    }

    public Long getId() { return dto.getId(); }

    public String getName() { return name.get(); }
    public void setName(String value) { name.set(value); }
    public StringProperty nameProperty() { return name; }

    public String getAddress() { return address.get(); }
    public void setAddress(String value) { address.set(value); }
    public StringProperty addressProperty() { return address; }

    @Override public String toString() { return name.get(); }
}
