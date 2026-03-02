package uk.ac.soton.comp2300.group42.energyclient.presentation.observable;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;

import static uk.ac.soton.comp2300.group42.energyclient.presentation.util.ModelUtils.updateIfChanged;

public class ObservableAppliance {

    private final Long id;
    private final ObjectProperty<ObservableHouse> house;
    private final StringProperty name;

    public ObservableAppliance(Appliance entity, ObservableHouse house) {
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

    public void updateFrom(Appliance entity, ObservableHouse house) {
        updateIfChanged(getName(), entity.name(), this::setName);
        updateIfChanged(getHouse(), house, this::setHouse);
    }

    public Long getId() { return id; }

    public String getName() { return name.get(); }
    public void setName(String value) { name.set(value); }
    public StringProperty nameProperty() { return name; }

    public ObservableHouse getHouse() { return house.get(); }
    public void setHouse(ObservableHouse value) { house.set(value); }
    public ObjectProperty<ObservableHouse> houseProperty() { return house; }

    @Override public String toString() { return name.get(); }
}
