package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageClient;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.ApplianceRepository;

import java.util.List;

@Singleton
public class LocalApplianceRepository implements ApplianceRepository {

    private final LocalStorageClient client;

    @Inject
    public LocalApplianceRepository(LocalStorageClient client) {
        this.client = client;
    }


    @Override
    public Appliance add(Appliance appliance) {
        return null;
    }

    @Override
    public Appliance get(Long houseId, Long applianceId) {
        return null;
    }

    @Override
    public List<Appliance> getAll(Long houseId) {
        return List.of();
    }

    @Override
    public Appliance update(Appliance appliance) {
        return null;
    }

    @Override
    public void delete(Long houseId, Long applianceId) {

    }
}
