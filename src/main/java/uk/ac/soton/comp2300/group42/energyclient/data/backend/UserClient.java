package uk.ac.soton.comp2300.group42.energyclient.data.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.house.CreateHouseRequest;
import uk.ac.soton.comp2300.group42.house.HouseResponse;
import uk.ac.soton.comp2300.group42.housemate.HousemateResponse;
import uk.ac.soton.comp2300.group42.preferences.PreferencesResponse;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.user.*;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Singleton
public class UserClient extends BaseApiClient {

    @Inject
    public UserClient(AuthenticatedHttpClient httpClient, ObjectMapper mapper) {
        super(httpClient, mapper);
    }

    public UserResponse findCurrentUser() {
        return get("users/me", UserResponse.class);
    }

    public PreferencesResponse findPreferences() {
        return get("users/me/preferences", PreferencesResponse.class);
    }

    // GET /users/me/houses
    public List<HouseResponse> findHousesForCurrentUser() {
        return List.of(
                new HouseResponse(1L, "House 1", "123 Main Street", ZoneId.systemDefault(), Role.OWNER),
                new HouseResponse(2L, "House 2", "456 Oak Lane", ZoneId.systemDefault(), Role.RESIDENT)
        );
    }

    // GET /houses/{houseId}
    public Optional<HouseResponse> findHouseById(Long houseId) {
        return findHousesForCurrentUser().stream()
                .filter(house -> house.id().equals(houseId))
                .findFirst();
    }

    // GET /houses/{houseId}/users
    // Does NOT include the current user.
    // Current user is fetched separately by findCurrentUserByHouseId
    public List<HousemateResponse> findAllByHouseId(Long houseId) {
        return List.of(
                new HousemateResponse(2L, 2L, "Jane Doe", "janedoe@soton.ac.uk", Role.RESIDENT)
        );
    }

    // GET /houses/{houseId}/me
    public Optional<HousemateResponse> findCurrentUserByHouseId(Long houseId) {
        return Optional.of(new HousemateResponse(1L, 1L, "John Doe", "johndoe@soton.ac.uk", Role.OWNER));
    }

    // POST /houses
    // For new users and users whose only house was deleted
    public HouseResponse createDefaultHouse() {
        // return new HouseResponse(3L, "Primary House", "789 Pine Road", ZoneId.systemDefault(), Role.OWNER);

        CreateHouseRequest request = new CreateHouseRequest("Primary House", "789 Pine Road", ZoneId.systemDefault());

        return post("houses", request, HouseResponse.class);
    }
}