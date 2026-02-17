package uk.ac.soton.comp2300.group42.energyclient.data.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.soton.comp2300.group42.energyclient.data.AuthenticatedHttpClient;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.*;

import java.io.IOException;
import java.net.http.HttpResponse;
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
        httpClient.setTokenPair(null, null);
    }

    public boolean login(String email, String password) {
        LoginDTO dto = new LoginDTO(email, password);

        String json;
        try { json = mapper.writeValueAsString(dto); }
        catch (JsonProcessingException e) { throw new RuntimeException("Failed to serialize user for login", e); }

        HttpResponse<String> response;
        try { response = httpClient.post("auth/login", json); }
        catch (IOException e) { throw new RuntimeException("An I/O error occurred when sending or receiving, or the client shut down during login", e); }
        catch (InterruptedException e) { throw new RuntimeException("The API call was interrupted during login", e); }

        if (response.statusCode() != 200)
            return false;

        AuthResponseDTO auth;
        try { auth = mapper.readValue(response.body(), AuthResponseDTO.class); }
        catch (JsonProcessingException e) { throw new RuntimeException("Failed to deserialize auth response", e); }

        httpClient.setTokenPair(auth.accessToken(), auth.refreshToken());
        return true;
    }

    public boolean register(String name, String email, String password) {
        RegistrationDTO dto = new RegistrationDTO(name, email, password);

        String json;
        try { json = mapper.writeValueAsString(dto); }
        catch (JsonProcessingException e) { throw new RuntimeException("Failed to serialize user for registration", e); }

        HttpResponse<String> response;
        try { response = httpClient.post("auth/register", json); }
        catch (IOException e) { throw new RuntimeException("An I/O error occurred when sending or receiving, or the client shut down during registration", e); }
        catch (InterruptedException e) { throw new RuntimeException("The API call was interrupted during registration", e); }

        if (response.statusCode() == 200) {
            login(email, password);
            return true;
        }

        System.out.println(response.statusCode());

        throw new RuntimeException("Signup failed: " + response.body());
    }

    // GET /users/me
    public UserDTO findCurrentUser() {
        HttpResponse<String> response;
        try { response = httpClient.get("users/me"); }
        catch (IOException e) { throw new RuntimeException("An I/O error occurred when sending or receiving, or the client shut down while fetching current user", e); }
        catch (InterruptedException e) { throw new RuntimeException("The API call was interrupted while fetching current user", e); }

        if (response.statusCode() != 200)
            throw new RuntimeException("Failed to fetch current user: " + response.statusCode());

        try { return mapper.readValue(response.body(), UserDTO.class); }
        catch (JsonProcessingException e) { throw new RuntimeException("Failed to deserialize user", e); }
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