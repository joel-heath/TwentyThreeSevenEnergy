package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.activation.ActivationType;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageClient;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageData;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.ApiException;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.UnauthorizedException;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Activation;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.ActivationRepository;

import java.util.List;
import java.util.Objects;

@Singleton
public class LocalActivationRepository implements ActivationRepository {

    private final LocalStorageClient client;
    private final LocalStorageData data;

    @Inject
    public LocalActivationRepository(LocalStorageClient client) {
        this.client = client;
        this.data = client.getData();
    }

    @Override
    public Activation add(Activation activation) {
        validateRequestFields(activation);

        Activation newActivation = new Activation(
                data.nextActivationId(),
                activation.applianceId(),
                activation.houseId(),
                activation.type(),
                activation.activationTime(),
                activation.activationDate(),
                activation.recursMonday(),
                activation.recursTuesday(),
                activation.recursWednesday(),
                activation.recursThursday(),
                activation.recursFriday(),
                activation.recursSaturday(),
                activation.recursSunday()
        );

        data.activations.put(newActivation.id(), newActivation);
        client.saveData();
        return newActivation;
    }

    @Override
    public Activation get(Long houseId, Long activationId) {
        return validateExists(houseId, activationId);
    }

    @Override
    public List<Activation> getAll(Long houseId) {
        validateHouseExists(houseId);
        return data.activations.values().stream()
                .filter(a -> Objects.equals(houseId, a.houseId()))
                .toList();
    }

    @Override
    public Activation update(Activation activation) {
        validateRequestFields(activation);
        validateExists(activation.houseId(), activation.id());

        data.activations.put(activation.id(), activation);
        client.saveData();
        return activation;
    }

    @Override
    public void delete(Long houseId, Long activationId) {
        validateExists(houseId, activationId);

        data.activations.remove(activationId);
        client.saveData();
    }

    private void validateHouseExists(Long houseId) {
        if (Objects.isNull(houseId))
            throw new ApiException("House ID is required", 400);

        if (!data.houses.containsKey(houseId))
            throw new ApiException("House not found", 404);
    }

    private void validateApplianceExists(Long houseId, Long applianceId) {
        if (Objects.isNull(applianceId))
            throw new ApiException("Appliance ID is required", 400);

        Appliance appliance = data.appliances.get(applianceId);

        if (Objects.isNull(appliance))
            throw new ApiException("Appliance not found", 404);

        if (!Objects.equals(houseId, appliance.houseId()))
            throw new UnauthorizedException("Appliance does not belong to this house");
    }

    private Activation validateExists(Long houseId, Long activationId) {
        validateHouseExists(houseId);

        if (Objects.isNull(activationId))
            throw new ApiException("Activation ID is required", 400);

        Activation activation = data.activations.get(activationId);

        if (Objects.isNull(activation))
            throw new ApiException("Activation not found", 404);

        if (!Objects.equals(houseId, activation.houseId()))
            throw new UnauthorizedException("Activation does not belong to this house");

        return activation;
    }

    private void validateRequestFields(Activation activation) {
        if (Objects.isNull(activation))
            throw new ApiException("Activation is required", 400);

        validateHouseExists(activation.houseId());

        validateApplianceExists(activation.houseId(), activation.applianceId());

        if (Objects.isNull(activation.type()))
            throw new ApiException("Activation type is required", 400);

        if (Objects.isNull(activation.activationTime()))
            throw new ApiException("Activation time is required", 400);

        if (activation.type() == ActivationType.NON_RECURRING) {
            if (Objects.isNull(activation.activationDate()))
                throw new ApiException("Activation date is required for non-recurring activations", 400);
        }
        else {
            if (activation.recursMonday() == null ||
                activation.recursTuesday() == null ||
                activation.recursWednesday() == null ||
                activation.recursThursday() == null ||
                activation.recursFriday() == null ||
                activation.recursSaturday() == null ||
                activation.recursSunday() == null)
                throw new ApiException("Recurrence fields are required for recurring activations", 400);
        }
    }
}
