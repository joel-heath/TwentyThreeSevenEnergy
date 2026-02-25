package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageClient;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.HouseRepository;

import java.util.List;

@Singleton
public class LocalHouseRepository implements HouseRepository {

    private final LocalStorageClient client;

    @Inject
    public LocalHouseRepository(LocalStorageClient client) {
        this.client = client;
    }

    @Override
    public House add() {
        return null;
    }

    @Override
    public House add(House house) {
        return null;
    }

    @Override
    public House get(Long houseId) {
        return null;
    }

    @Override
    public List<House> getCurrentUserHouses() {
        return List.of();
    }

    @Override
    public House update(House house) {
        return null;
    }

    @Override
    public void delete(Long houseId) {

    }

    @Override
    public Housemate getCurrentUserAsHousemate(Long houseId) {
        return null;
    }

    @Override
    public List<Housemate> getHousemates(Long houseId) {
        return List.of();
    }
}
