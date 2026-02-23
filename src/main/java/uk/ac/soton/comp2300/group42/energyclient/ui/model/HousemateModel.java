package uk.ac.soton.comp2300.group42.energyclient.ui.model;

import javafx.beans.property.*;
import uk.ac.soton.comp2300.group42.energyclient.data.backend.Role;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.HousemateDTO;

import static uk.ac.soton.comp2300.group42.energyclient.ui.util.ModelUtils.updateIfChanged;

public class HousemateModel {

    private final HousemateDTO dto;
    private final StringProperty forename;
    private final StringProperty surname;
    private final StringProperty email;
    private final ObjectProperty<HouseModel> house;
    private final ObjectProperty<Role> role;

    public HousemateModel(HousemateDTO dto, HouseModel house) {
        this.dto = dto;
        this.house = new SimpleObjectProperty<>(house);
        this.forename = new SimpleStringProperty(dto.getForename());
        this.surname = new SimpleStringProperty(dto.getSurname());
        this.email = new SimpleStringProperty(dto.getEmail());
        this.role = new SimpleObjectProperty<>(dto.getRole());
    }

    public HousemateDTO commit() {
        dto.setForename(forename.get());
        dto.setSurname(surname.get());
        dto.setEmail(email.get());
        dto.setRole(role.get());
        return dto;
    }

    public void updateFrom(HousemateDTO dto, HouseModel house) {
        updateIfChanged(getForename(), dto.getForename(), this::setForename);
        updateIfChanged(getSurname(), dto.getSurname(), this::setSurname);
        updateIfChanged(getEmail(), dto.getEmail(), this::setEmail);
        updateIfChanged(getRole(), dto.getRole(), this::setRole);
        updateIfChanged(getHouse(), house, this::setHouse);
    }

    public Long getId() { return dto.getId(); }
    public Long getHouseId() { return dto.getHouseId(); }

    public String getForename() { return forename.get(); }
    public void setForename(String value) { forename.set(value); }
    public StringProperty forenameProperty() { return forename; }

    public String getSurname() { return surname.get(); }
    public void setSurname(String value) { surname.set(value); }
    public StringProperty surnameProperty() { return surname; }

    public String getEmail() { return email.get(); }
    public void setEmail(String value) { email.set(value); }
    public StringProperty emailProperty() { return email; }

    public HouseModel getHouse() { return house.get(); }
    public void setHouse(HouseModel house) { this.house.set(house); }
    public ObjectProperty<HouseModel> houseProperty() { return house; }

    public Role getRole() { return role.get(); }
    public void setRole(Role role) { this.role.set(role); }
    public ObjectProperty<Role> roleProperty() { return role; }

    @Override public String toString() { return forename.get() + " " + surname.get(); }
}
