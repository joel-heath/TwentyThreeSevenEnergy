package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.backend.HouseClient;
import uk.ac.soton.comp2300.group42.energyclient.data.mapper.HouseMapper;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.HouseRepository;
import uk.ac.soton.comp2300.group42.house.CreateHouseRequest;

import java.time.ZoneId;
import java.util.List;

@Singleton
public class RemoteHouseRepository implements HouseRepository {

    private final HouseClient client;
    private final HouseMapper mapper;

    @Inject
    public RemoteHouseRepository(HouseClient client, HouseMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public House add() {
        CreateHouseRequest request = new CreateHouseRequest("Primary House", "No address set", ZoneId.systemDefault());
        return mapper.toHouse(client.postHouse(request));
    }

    @Override
    public House add(House house) {
        return mapper.toHouse(client.postHouse(mapper.toCreateHouseRequest(house)));
    }

    @Override
    public House get(Long houseId) {
        return mapper.toHouse(client.fetchHouse(houseId));
    }

    @Override
    public List<House> getCurrentUserHouses() {
        return client.fetchMyHouses().stream().map(mapper::toHouse).toList();
    }

    @Override
    public House update(House house) {
        return mapper.toHouse(client.putHouse(house.id(), mapper.toUpdateHouseRequest(house)));
    }

    @Override
    public void delete(Long houseId) {
        client.deleteHouse(houseId);
    }

    @Override
    public Housemate getCurrentUserAsHousemate(Long houseId) {
        return mapper.toHousemate(client.fetchMeAsHousemate(houseId));
    }

    @Override
    public List<Housemate> getHousemates(Long houseId) {
        return client.fetchHousemates(houseId).stream().map(mapper::toHousemate).toList();
    }
}
