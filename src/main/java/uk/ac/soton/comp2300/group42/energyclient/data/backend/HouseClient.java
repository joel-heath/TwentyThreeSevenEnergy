package uk.ac.soton.comp2300.group42.energyclient.data.backend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.BackendMapper;
import uk.ac.soton.comp2300.group42.house.CreateHouseRequest;
import uk.ac.soton.comp2300.group42.house.HouseResponse;
import uk.ac.soton.comp2300.group42.house.UpdateHouseRequest;
import uk.ac.soton.comp2300.group42.housemate.HousemateResponse;

import java.util.List;

@Singleton
public class HouseClient extends BaseApiClient {

    @Inject
    public HouseClient(AuthenticatedHttpClient httpClient, @BackendMapper ObjectMapper mapper) {
        super(httpClient, mapper);
    }

    public HouseResponse postHouse(CreateHouseRequest request) {
        return post("houses", request, new TypeReference<>() {});
    }

    public HouseResponse fetchHouse(Long houseId) {
        return get("houses/" + houseId, new TypeReference<>() {});
    }

    public List<HouseResponse> fetchMyHouses() {
        return get("houses/me", new TypeReference<>() {});
    }

    public HouseResponse putHouse(Long houseId, UpdateHouseRequest request) {
        return put("houses/" + houseId, request, new TypeReference<>() {});
    }

    public void deleteHouse(Long houseId) {
        delete("houses/" + houseId);
    }

    public HousemateResponse fetchMeAsHousemate(Long houseId) {
        return get("houses/" + houseId + "/me", new TypeReference<>() {});
    }

    public List<HousemateResponse> fetchHousemates(Long houseId) {
        return get("houses/" + houseId + "/housemates", new TypeReference<>() {});
    }
}
