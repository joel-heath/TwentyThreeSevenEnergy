package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageClient;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageData;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.ApplianceRepository;

import java.util.List;
import java.util.Objects;

import static uk.ac.soton.comp2300.group42.energyclient.data.repository.LocalRepositoryUtils.throwApiException;

@Singleton
public class LocalApplianceRepository implements ApplianceRepository {

    private final LocalStorageClient client;
    private final LocalStorageData data;

    @Inject
    public LocalApplianceRepository(LocalStorageClient client) {
        this.client = client;
        this.data = client.getData();
    }

    @Override
    public Appliance add(Appliance appliance) {
        validateRequestFields(appliance);

        Appliance newAppliance = new Appliance(
                data.nextApplianceId(),
                appliance.houseId(),
                appliance.name()
        );

        data.appliances.put(newAppliance.id(), newAppliance);
        client.saveData();
        return newAppliance;
    }

    @Override
    public Appliance get(Long houseId, Long applianceId) {
        return validateRequestExists(houseId, applianceId);
    }

    @Override
    public List<Appliance> getAll(Long houseId) {
        validateRequestExists(houseId);

        return data.appliances.values().stream()
                .filter(a -> Objects.equals(a.houseId(), houseId))
                .toList();
    }

    @Override
    public Appliance update(Appliance appliance) {
        validateRequestFields(appliance);
        validateRequestExists(appliance.houseId(), appliance.id());

        data.appliances.put(appliance.id(), appliance);
        client.saveData();
        return appliance;
    }

    @Override
    public void delete(Long houseId, Long applianceId) {
        validateRequestExists(houseId, applianceId);

        data.appliances.remove(applianceId);
        client.saveData();
    }

    private void validateRequestExists(Long houseId) {
        if (Objects.isNull(houseId))
            throwApiException(400, "House ID is required");

        if (!data.houses.containsKey(houseId))
            throwApiException(404, "House with id " + houseId + " not found");
    }

    private Appliance validateRequestExists(Long houseId, Long applianceId) {
        validateRequestExists(houseId);

        if (Objects.isNull(applianceId))
            throwApiException(400, "Appliance ID is required");

        Appliance appliance = data.appliances.get(applianceId);

        if (Objects.isNull(appliance))
            throwApiException(404, "Appliance not found");

        if (!Objects.equals(houseId, appliance.houseId()))
            throwApiException(401, "Appliance does not belong to this house");

        return appliance;
    }

    private void validateRequestFields(Appliance appliance) {
        if (Objects.isNull(appliance))
            throwApiException(400, "Appliance is required");

        validateRequestExists(appliance.houseId());

        if (Objects.isNull(appliance.name()) || appliance.name().isBlank())
            throwApiException(400, "Appliance name is required");
    }
}
