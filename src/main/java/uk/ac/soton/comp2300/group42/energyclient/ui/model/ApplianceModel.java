package uk.ac.soton.comp2300.group42.energyclient.ui.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;

import static uk.ac.soton.comp2300.group42.energyclient.ui.util.ModelUtils.updateIfChanged;

public class ApplianceModel {

    private final Long id;
    private final ObjectProperty<HouseModel> house;
    private final StringProperty name;

    public ApplianceModel(Appliance entity, HouseModel house) {
        this.id = entity.id();
        this.name = new SimpleStringProperty(entity.name());
        this.house = new SimpleObjectProperty<>(house);
    }

    public Appliance commit() {
        return new Appliance(
                getId(),
                getHouse().getId(),
                getName()
        );
    }

    public void updateFrom(Appliance entity) {
        updateIfChanged(getName(), entity.name(), this::setName);
    }

    public Long getId() { return id; }

    public String getName() { return name.get(); }
    public void setName(String value) { name.set(value); }
    public StringProperty nameProperty() { return name; }

    public HouseModel getHouse() { return house.get(); }
    public void setHouse(HouseModel value) { house.set(value); }
    public ObjectProperty<HouseModel> houseProperty() { return house; }

    @Override public String toString() { return name.get(); }
}
