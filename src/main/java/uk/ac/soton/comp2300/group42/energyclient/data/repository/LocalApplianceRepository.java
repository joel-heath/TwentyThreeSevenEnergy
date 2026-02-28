package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageClient;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageData;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.ApiException;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.UnauthorizedException;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.ApplianceRepository;

import java.util.List;
import java.util.Objects;

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
            throw new ApiException("House ID is required", 400);

        if (!data.houses.containsKey(houseId))
            throw new ApiException("House not found", 404);
    }

    private Appliance validateRequestExists(Long houseId, Long applianceId) {
        validateRequestExists(houseId);

        if (Objects.isNull(applianceId))
            throw new ApiException("Appliance ID is required", 400);

        Appliance appliance = data.appliances.get(applianceId);

        if (Objects.isNull(appliance))
            throw new ApiException("Appliance not found", 404);

        if (!Objects.equals(houseId, appliance.houseId()))
            throw new UnauthorizedException("Appliance does not belong to this house");

        return appliance;
    }

    private void validateRequestFields(Appliance appliance) {
        if (Objects.isNull(appliance))
            throw new ApiException("Appliance is required", 400);

        validateRequestExists(appliance.houseId());

        if (Objects.isNull(appliance.name()) || appliance.name().isBlank())
            throw new ApiException("Appliance name is required", 400);
    }
}
