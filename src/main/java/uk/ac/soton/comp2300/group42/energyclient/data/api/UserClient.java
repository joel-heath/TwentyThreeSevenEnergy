package uk.ac.soton.comp2300.group42.energyclient.data.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.soton.comp2300.group42.energyclient.data.AuthenticatedHttpClient;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.HouseDTO;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.HousemateDTO;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.PreferencesDTO;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.UserDTO;

import java.util.List;
import java.util.Optional;

public class UserClient {

    private final AuthenticatedHttpClient httpClient;
    private final ObjectMapper mapper;

    public UserClient(AuthenticatedHttpClient httpClient, ObjectMapper mapper) {
        this.httpClient = httpClient;
        this.mapper = mapper;
    }

    public void logout() {
        httpClient.setAccessToken(null);
    }

    // POST /auth/login
    public boolean login(String email, String password) {
        String token = "this-is-a-jwt-token-returned-by-the-api";
        httpClient.setAccessToken(token);
        return true;
    }

    // POST /auth/signup
    public boolean register(String email, String password) {
        // do some registering...

        return login(email, password);
    }

    // GET /users/me
    public UserDTO findCurrentUser() {
        return new UserDTO(1L, "John", "Doe", "johndoe@soton.ac.uk");
    }

    // GET /users/me/preferences
    public PreferencesDTO findPreferences() { return new PreferencesDTO(); }

    // GET /users/me/houses
    public List<HouseDTO> findHousesForCurrentUser() {
        return List.of(
                new HouseDTO(1L, "House 1", "123 Main Street"),
                new HouseDTO(2L, "House 2", "456 Oak Lane")
        );
    }

    // GET /houses/{houseId}
    public Optional<HouseDTO> findHouseById(Long houseId) {
        return findHousesForCurrentUser().stream()
                .filter(house -> house.getId().equals(houseId))
                .findFirst();
    }

    // GET /houses/{houseId}/users
    // Does NOT include the current user.
    // Current user is fetched separately by findCurrentUserByHouseId
    public List<HousemateDTO> findAllByHouseId(Long houseId) {
        return List.of(
                // new HousemateDTO(1L, "John", "Doe", "johndoe@soton.ac.uk", 1L, Role.OWNER),
                new HousemateDTO(2L, "Jane", "Doe", "janedoe@soton.ac.uk", 2L, Role.RESIDENT)
        );
    }

    // GET /houses/{houseId}/me
    public Optional<HousemateDTO> findCurrentUserByHouseId(Long houseId) {
        return Optional.of(new HousemateDTO(1L, "John", "Doe", "johndoe@soton.ac.uk", 1L, Role.OWNER));
    }

    // POST /houses
    // For new users and users whose only house was deleted
    public HouseDTO createDefaultHouse() {
        // in reality, we will create a new HouseDTO here,
        // pass it to the server which will populate the ID field and return a new DTO
        // then we will return that.
        // this will assign the current user as the owner.

        return new HouseDTO(3L, "Primary House", "789 Pine Road");
    }
}