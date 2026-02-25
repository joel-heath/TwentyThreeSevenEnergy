package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageClient;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Activation;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.ActivationRepository;

import java.util.List;

@Singleton
public class LocalActivationRepository implements ActivationRepository {

    private final LocalStorageClient client;

    @Inject
    public LocalActivationRepository(LocalStorageClient client) {
        this.client = client;
    }


    @Override
    public Activation add(Activation activation) {
        return null;
    }

    @Override
    public Activation get(Long houseId, Long activationId) {
        return null;
    }

    @Override
    public List<Activation> getAll(Long houseId) {
        return List.of();
    }

    @Override
    public Activation update(Activation activation) {
        return null;
    }

    @Override
    public void delete(Long houseId, Long activationId) {

    }
}
