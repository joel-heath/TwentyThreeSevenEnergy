package uk.ac.soton.comp2300.group42.energyclient.ui.model;

import javafx.beans.property.*;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;

import static uk.ac.soton.comp2300.group42.energyclient.ui.util.ModelUtils.updateIfChanged;

public class HousemateModel {

    private final Long id;
    private final StringProperty name;
    private final StringProperty email;
    private final ObjectProperty<HouseModel> house;
    private final ObjectProperty<Role> role;

    public HousemateModel(Housemate housemate, HouseModel house) {
        this.id = housemate.userId();
        this.house = new SimpleObjectProperty<>(house);
        this.name = new SimpleStringProperty(housemate.name());
        this.email = new SimpleStringProperty(housemate.email());
        this.role = new SimpleObjectProperty<>(housemate.role());
    }

    public Housemate commit() {
        return new Housemate(
                getId(),
                getHouse().getId(),
                getName(),
                getEmail(),
                getRole()
        );
    }

    public void updateFrom(Housemate housemate, HouseModel house) {
        updateIfChanged(getName(), housemate.name(), this::setName);
        updateIfChanged(getEmail(), housemate.email(), this::setEmail);
        updateIfChanged(getRole(), housemate.role(), this::setRole);
        updateIfChanged(getHouse(), house, this::setHouse);
    }

    public Long getId() { return id; }

    public String getName() { return name.get(); }
    public void setName(String value) { name.set(value); }
    public StringProperty nameProperty() { return name; }

    public String getEmail() { return email.get(); }
    public void setEmail(String value) { email.set(value); }
    public StringProperty emailProperty() { return email; }

    public HouseModel getHouse() { return house.get(); }
    public void setHouse(HouseModel house) { this.house.set(house); }
    public ObjectProperty<HouseModel> houseProperty() { return house; }

    public Role getRole() { return role.get(); }
    public void setRole(Role role) { this.role.set(role); }
    public ObjectProperty<Role> roleProperty() { return role; }

    @Override public String toString() { return getName(); }
}
