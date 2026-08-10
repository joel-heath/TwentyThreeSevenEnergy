package uk.ac.soton.comp2300.group42.energyclient.domain.repository;

import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;

import java.util.List;

public interface ApplianceRepository {
    Appliance add(Appliance appliance);
    Appliance get(Long houseId, Long applianceId);
    List<Appliance> getAll(Long houseId);
    Appliance update(Appliance appliance);
    void delete(Long houseId, Long applianceId);
}
