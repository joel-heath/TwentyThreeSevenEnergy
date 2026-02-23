package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageClient;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.HouseRepository;

import java.util.List;
import java.util.Optional;

@Singleton
public class LocalHouseRepository implements HouseRepository {

    private final LocalStorageClient client;

    @Inject
    public LocalHouseRepository(LocalStorageClient client) {
        this.client = client;
    }

    @Override
    public List<House> findHousesForCurrentUser() {
        return List.of();
    }

    @Override
    public Optional<House> findHouseById(Long houseId) {
        return Optional.empty();
    }

    @Override
    public List<Housemate> findAllByHouseId(Long houseId) {
        return List.of();
    }

    @Override
    public Optional<Housemate> findCurrentUserByHouseId(Long houseId) {
        return Optional.empty();
    }

    @Override
    public House createDefaultHouse() {
        return null;
    }
}
