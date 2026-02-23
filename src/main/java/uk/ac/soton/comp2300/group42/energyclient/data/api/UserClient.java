package uk.ac.soton.comp2300.group42.energyclient.data.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.AuthenticatedHttpClient;
import uk.ac.soton.comp2300.group42.house.CreateHouseRequest;
import uk.ac.soton.comp2300.group42.house.HouseResponse;
import uk.ac.soton.comp2300.group42.housemate.HousemateResponse;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;
import uk.ac.soton.comp2300.group42.preferences.PreferencesResponse;
import uk.ac.soton.comp2300.group42.preferences.Theme;
import uk.ac.soton.comp2300.group42.preferences.Mode;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.user.*;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Singleton
public class UserClient {

    private final AuthenticatedHttpClient httpClient;
    private final ObjectMapper mapper;

    @Inject
    public UserClient(AuthenticatedHttpClient httpClient, ObjectMapper mapper) {
        this.httpClient = httpClient;
        this.mapper = mapper;
    }

    public void logout() {
        httpClient.clearTokenPair();
    }

    public boolean isLoggedIn() {
        try {
            HttpResponse<String> response = httpClient.get("users/me");
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            System.err.println("Startup authentication check failed, server offline or I/O error: " + e.getMessage());
            return false;
        }
    }

    public boolean login(String email, String password) {
        LoginRequest dto = new LoginRequest(email, password);

        String json;
        try { json = mapper.writeValueAsString(dto); }
        catch (JsonProcessingException e) { throw new RuntimeException("Failed to serialize user for login", e); }

        HttpResponse<String> response;
        try { response = httpClient.post("auth/login", json); }
        catch (IOException e) { throw new RuntimeException("An I/O error occurred when sending or receiving, or the client shut down during login", e); }
        catch (InterruptedException e) { throw new RuntimeException("The API call was interrupted during login", e); }

        if (response.statusCode() != 200)
            return false;

        AuthResponse auth;
        try { auth = mapper.readValue(response.body(), AuthResponse.class); }
        catch (JsonProcessingException e) { throw new RuntimeException("Failed to deserialize auth response", e); }

        httpClient.setTokenPair(auth.accessToken(), auth.refreshToken());
        return true;
    }

    public boolean register(String name, String email, String password) {
        RegistrationRequest dto = new RegistrationRequest(name, email, password);

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
    public UserResponse findCurrentUser() {
        HttpResponse<String> response;
        try { response = httpClient.get("users/me"); }
        catch (IOException e) { throw new RuntimeException("An I/O error occurred when sending or receiving, or the client shut down while fetching current user", e); }
        catch (InterruptedException e) { throw new RuntimeException("The API call was interrupted while fetching current user", e); }

        if (response.statusCode() != 200)
            throw new RuntimeException("Failed to fetch current user: " + response.statusCode());

        try { return mapper.readValue(response.body(), UserResponse.class); }
        catch (JsonProcessingException e) { throw new RuntimeException("Failed to deserialize user", e); }
    }

    // GET /users/me/preferences
    public PreferencesResponse findPreferences() {
        return new PreferencesResponse(
            0L, false, ColorVision.TYPICAL, Theme.LIGHT, Mode.SIMPLE, false, 1.0, 0L
        );
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

        String json;
        try { json = mapper.writeValueAsString(request); }
        catch (JsonProcessingException e) { throw new RuntimeException("Failed to serialize", e); }

        HttpResponse<String> response;
        try { response = httpClient.post("houses", json); }
        catch (IOException e) { throw new RuntimeException("An I/O error occurred when sending or receiving, or the client shut down", e); }
        catch (InterruptedException e) { throw new RuntimeException("The API call was interrupted", e); }

        if (response.statusCode() != 201)
            throw new RuntimeException("Failed to create house: " + response.statusCode());

        try { return mapper.readValue(response.body(), HouseResponse.class); }
        catch (JsonProcessingException e) { throw new RuntimeException("Failed to deserialize", e); }
    }
}