package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import uk.ac.soton.comp2300.group42.energyclient.data.backend.ActivationClient;
import uk.ac.soton.comp2300.group42.energyclient.data.mapper.ActivationMapper;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Activation;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.ActivationRepository;

import java.util.List;

public class RemoteActivationRepository implements ActivationRepository {

    private final ActivationClient client;
    private final ActivationMapper mapper;

    @Inject
    public RemoteActivationRepository(ActivationClient client, ActivationMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public Activation add(Activation activation) {
        return mapper.toActivation(client.postActivation(activation.houseId(), mapper.toCreateActivationRequest(activation)));
    }

    @Override
    public Activation get(Long houseId, Long activationId) {
        return mapper.toActivation(client.fetchActivation(houseId, activationId));
    }

    @Override
    public List<Activation> getAll(Long houseId) {
        return client.fetchAllActivations(houseId).stream()
                .map(mapper::toActivation)
                .toList();
    }

    @Override
    public Activation update(Activation activation) {
        return mapper.toActivation(client.putActivation(activation.houseId(), activation.id(), mapper.toUpdateActivationRequest(activation)));
    }

    @Override
    public void delete(Long houseId, Long activationId) {
        client.deleteActivation(houseId, activationId);
    }
}
