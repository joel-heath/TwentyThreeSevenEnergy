package uk.ac.soton.comp2300.group42.energyclient.presentation.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;

import java.time.ZoneId;

import static uk.ac.soton.comp2300.group42.energyclient.presentation.util.ModelUtils.updateIfChanged;

public class HouseModel {

    private final Long id;
    private final StringProperty name;
    private final StringProperty address;
    private final ObjectProperty<ZoneId> timezone;
    private final ObjectProperty<Role> role;

    public HouseModel(House house) {
        this.id = house.id();
        this.name = new SimpleStringProperty(house.name());
        this.address = new SimpleStringProperty(house.address());
        this.timezone = new SimpleObjectProperty<>(house.timezone());
        this.role = new SimpleObjectProperty<>(house.role());
    }

    public House commit() {
        return new House(
                getId(),
                getName(),
                getAddress(),
                getTimezone(),
                getRole()
        );
    }

    public void updateFrom(House entity) {
        updateIfChanged(getName(), entity.name(), this::setName);
        updateIfChanged(getAddress(), entity.address(), this::setAddress);
        updateIfChanged(getTimezone(), entity.timezone(), this::setTimezone);
        updateIfChanged(getRole(), entity.role(), this::setRole);
    }

    public Long getId() { return id; }

    public String getName() { return name.get(); }
    public void setName(String value) { name.set(value); }
    public StringProperty nameProperty() { return name; }

    public String getAddress() { return address.get(); }
    public void setAddress(String value) { address.set(value); }
    public StringProperty addressProperty() { return address; }

    public ZoneId getTimezone() { return timezone.get(); }
    public void setTimezone(ZoneId value) { timezone.set(value); }
    public ObjectProperty<ZoneId> timezoneProperty() { return timezone; }

    public Role getRole() { return role.get(); }
    public void setRole(Role value) { role.set(value); }
    public ObjectProperty<Role> roleProperty() { return role; }

    @Override public String toString() { return name.get(); }
}
