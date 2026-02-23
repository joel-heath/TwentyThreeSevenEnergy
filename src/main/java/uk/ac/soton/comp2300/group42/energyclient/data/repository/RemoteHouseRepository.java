package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.backend.HouseClient;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.HouseRepository;
import uk.ac.soton.comp2300.group42.house.HouseResponse;
import uk.ac.soton.comp2300.group42.housemate.HousemateResponse;

import java.util.List;
import java.util.Optional;

@Singleton
public class RemoteHouseRepository implements HouseRepository {

    private final HouseClient client;

    @Inject
    public RemoteHouseRepository(HouseClient client) {
        this.client = client;
    }

    @Override
    public List<House> findHousesForCurrentUser() {
         return client.findHousesForCurrentUser().stream().map(this::mapToHouse).toList();
    }

    @Override
    public Optional<House> findHouseById(Long houseId) {
        return client.findHouseById(houseId).map(this::mapToHouse);
    }

    @Override
    public List<Housemate> findAllByHouseId(Long houseId) {
        return client.findAllByHouseId(houseId).stream().map(this::mapToHousemate).toList();
    }

    @Override
    public Optional<Housemate> findCurrentUserByHouseId(Long houseId) {
        return client.findCurrentUserByHouseId(houseId).map(this::mapToHousemate);
    }

    @Override
    public House createDefaultHouse() {
        return mapToHouse(client.createDefaultHouse());
    }

    private House mapToHouse(HouseResponse response) {
        return new House(
                response.id(),
                response.name(),
                response.address(),
                response.timezone(),
                response.role()
        );
    }

    private Housemate mapToHousemate(HousemateResponse response) {
        return new Housemate(
                response.userId(),
                response.houseId(),
                response.role()
        );
    }
}
