package uk.ac.soton.comp2300.group42.energyclient.data.backend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.house.CreateHouseRequest;
import uk.ac.soton.comp2300.group42.house.HouseResponse;
import uk.ac.soton.comp2300.group42.housemate.HousemateResponse;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Singleton
public class HouseClient extends BaseApiClient {

    @Inject
    public HouseClient(AuthenticatedHttpClient httpClient, ObjectMapper mapper) {
        super(httpClient, mapper);
    }

    public List<HouseResponse> findHousesForCurrentUser() {
        return get("users/me/houses", new TypeReference<>() {});
    }

    public Optional<HouseResponse> findHouseById(Long houseId) {
        return get("houses/" + houseId, new TypeReference<>() {});
    }

    public List<HousemateResponse> findAllByHouseId(Long houseId) {
        return get("houses/" + houseId + "/users", new TypeReference<>() {});
    }

    public Optional<HousemateResponse> findCurrentUserByHouseId(Long houseId) {
        return get("houses/" + houseId + "/me", new TypeReference<>() {});
    }

    public HouseResponse createDefaultHouse() {
        CreateHouseRequest request = new CreateHouseRequest("Primary House", "No address set", ZoneId.systemDefault());
        return post("houses", request, new TypeReference<>() {});
    }
}
