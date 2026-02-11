package uk.ac.soton.comp2300.group42.energyclient.data.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.soton.comp2300.group42.energyclient.data.AuthenticatedHttpClient;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.UserDTO;

import java.util.Optional;

public class UserClient {

    private final AuthenticatedHttpClient httpClient;
    private final ObjectMapper mapper;

    public UserClient(AuthenticatedHttpClient httpClient, ObjectMapper mapper) {
        this.httpClient = httpClient;
        this.mapper = mapper;
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



    public void logout() {
        httpClient.setAccessToken(null);
    }

    // GET /users/profile
    public UserDTO getCurrentUser() {
        return new UserDTO(1L, "John", "Doe", "johndoe@soton.ac.uk");
    }

    // GET /users/{id}
    // Returns public (nonsensitive) info only.
    // For getting names of housemates for displaying on the leaderboard etc.
    public Optional<UserDTO> findPublicProfileById(Long id) {
        return Optional.of(new UserDTO(id, "Jane", "Doe", null));
    }
}