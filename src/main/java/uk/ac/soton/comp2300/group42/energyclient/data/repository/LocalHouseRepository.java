package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageClient;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageData;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.HouseRepository;

import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

import static uk.ac.soton.comp2300.group42.energyclient.data.repository.LocalRepositoryUtils.throwApiException;

@Singleton
public class LocalHouseRepository implements HouseRepository {

    private final LocalStorageClient client;
    private final LocalStorageData data;

    @Inject
    public LocalHouseRepository(LocalStorageClient client) {
        this.client = client;
        this.data = client.getData();
    }

    @Override
    public House add() {
        House newHouse = new House(
                data.nextHouseId(),
                "Primary House",
                "No address set",
                ZoneId.systemDefault(),
                Role.OWNER
        );

        data.houses.put(newHouse.id(), newHouse);
        client.saveData();
        return newHouse;
    }

    @Override
    public House add(House house) {
        validateRequest(house);

        House newHouse = new House(
                data.nextHouseId(),
                house.name(),
                house.address(),
                house.timezone(),
                Role.OWNER
        );

        data.houses.put(newHouse.id(), newHouse);
        client.saveData();
        return newHouse;
    }

    @Override
    public House get(Long houseId) {
        return validateRequest(houseId);
    }

    @Override
    public List<House> getAll() {
        return data.houses.values().stream().toList();
    }

    @Override
    public House update(House house) {
        validateRequest(house);
        validateRequest(house.id());

        House updatedHouse = new House(
                house.id(),
                house.name(),
                house.address(),
                house.timezone(),
                Role.OWNER
        );

        data.houses.put(updatedHouse.id(), updatedHouse);
        client.saveData();
        return house;
    }

    @Override
    public void delete(Long houseId) {
        validateRequest(houseId);

        data.houses.remove(houseId);
        client.saveData();
    }

    @Override
    public Housemate getCurrentUserAsHousemate(Long houseId) {
        validateRequest(houseId);

        return new Housemate(
                data.user.id(),
                houseId,
                data.user.name(),
                data.user.email(),
                Role.OWNER
        );
    }

    @Override
    public List<Housemate> getHousemates(Long houseId) {
        validateRequest(houseId);

        return List.of(getCurrentUserAsHousemate(houseId));
    }

    private House validateRequest(Long houseId) {
        if (Objects.isNull(houseId))
            throwApiException(400, "House ID is required");

        House house = data.houses.get(houseId);

        if (Objects.isNull(house))
            throwApiException(404, "House not found");

        return house;
    }

    private void validateRequest(House house) {
        if (Objects.isNull(house))
            throwApiException(400, "House is required");

        if (Objects.isNull(house.name()) || house.name().isBlank())
            throwApiException(400, "House name is required");

        if (Objects.isNull(house.address()) || house.address().isBlank())
            throwApiException(400, "House address is required");

        if (Objects.isNull(house.timezone()))
            throwApiException(400, "House timezone is required");
    }
}
