package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.backend.ApplianceClient;
import uk.ac.soton.comp2300.group42.energyclient.data.mapper.ApplianceMapper;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.ApplianceRepository;

import java.util.List;

@Singleton
public class RemoteApplianceRepository implements ApplianceRepository {

    private final ApplianceClient client;
    private final ApplianceMapper mapper;

    @Inject
    public RemoteApplianceRepository(ApplianceClient client, ApplianceMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public Appliance add(Appliance appliance) {
        return mapper.toAppliance(client.postAppliance(appliance.houseId(), mapper.toCreateApplianceRequest(appliance)));
    }

    @Override
    public Appliance get(Long houseId, Long applianceId) {
        return mapper.toAppliance(client.fetchAppliance(houseId, applianceId));
    }

    @Override
    public List<Appliance> getAll(Long houseId) {
        return client.fetchAllAppliances(houseId).stream()
                .map(mapper::toAppliance)
                .toList();
    }

    @Override
    public Appliance update(Appliance appliance) {
        return mapper.toAppliance(client.putAppliance(appliance.houseId(), appliance.id(), mapper.toUpdateApplianceRequest(appliance)));
    }

    @Override
    public void delete(Long houseId, Long applianceId) {
        client.deleteAppliance(houseId, applianceId);
    }
}
