package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.activation.ActivationType;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageClient;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageData;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Activation;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.ActivationRepository;

import java.util.List;
import java.util.Objects;

import static uk.ac.soton.comp2300.group42.energyclient.data.repository.LocalRepositoryUtils.throwApiException;

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
        client.saveDataAsync();
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
        client.saveDataAsync();
        return activation;
    }

    @Override
    public void delete(Long houseId, Long activationId) {
        validateExists(houseId, activationId);

        data.activations.remove(activationId);
        client.saveDataAsync();
    }

    private void validateHouseExists(Long houseId) {
        if (Objects.isNull(houseId))
            throwApiException(400, "House ID is required");

        if (!data.houses.containsKey(houseId))
            throwApiException(404, "House not found");
    }

    private void validateApplianceExists(Long houseId, Long applianceId) {
        if (Objects.isNull(applianceId))
            throwApiException(400, "Appliance ID is required");

        Appliance appliance = data.appliances.get(applianceId);

        if (Objects.isNull(appliance))
            throwApiException(404, "Appliance not found");

        if (!Objects.equals(houseId, appliance.houseId()))
            throwApiException(401, "Appliance does not belong to this house");
    }

    private Activation validateExists(Long houseId, Long activationId) {
        validateHouseExists(houseId);

        if (Objects.isNull(activationId))
            throwApiException(400, "Activation ID is required");

        Activation activation = data.activations.get(activationId);

        if (Objects.isNull(activation))
            throwApiException(404, "Activation not found");

        if (!Objects.equals(houseId, activation.houseId()))
            throwApiException(401, "Activation does not belong to this house");

        return activation;
    }

    private void validateRequestFields(Activation activation) {
        if (Objects.isNull(activation))
            throwApiException(400, "Activation is required");

        validateHouseExists(activation.houseId());

        validateApplianceExists(activation.houseId(), activation.applianceId());

        if (Objects.isNull(activation.type()))
            throwApiException(400, "Activation type is required");

        if (Objects.isNull(activation.activationTime()))
            throwApiException(400, "Activation time is required");

        if (activation.type() == ActivationType.NON_RECURRING) {
            if (Objects.isNull(activation.activationDate()))
                throwApiException(400, "Activation date is required for non-recurring activations");
        }
        else {
            if (activation.recursMonday() == null ||
                activation.recursTuesday() == null ||
                activation.recursWednesday() == null ||
                activation.recursThursday() == null ||
                activation.recursFriday() == null ||
                activation.recursSaturday() == null ||
                activation.recursSunday() == null)
                throwApiException(400, "Recurrence fields are required for recurring activations");
        }
    }
}
