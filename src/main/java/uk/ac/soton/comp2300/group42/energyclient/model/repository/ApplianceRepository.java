package uk.ac.soton.comp2300.group42.energyclient.model.repository;

import uk.ac.soton.comp2300.group42.energyclient.model.entity.Appliance;
import java.util.List;
import java.util.Optional;

// Not yet implemented. TODO: Sprint 2
// Currently contains hard-coded testing data

public class ApplianceRepository {

    public Optional<Appliance> findById(int id) {
        return Optional.of(new Appliance(id, "Dishwasher"));
    }

    public List<Appliance> findAll() {
        return List.of(
            new Appliance(1, "Dishwasher"),
            new Appliance(2, "Washing Machine"),
            new Appliance(3, "Tumble Dryer"),
            new Appliance(4, "Oven"));
    }

    public Appliance save(Appliance appliance) {
        return appliance;
    }

    public void delete(Appliance appliance) { }

}
