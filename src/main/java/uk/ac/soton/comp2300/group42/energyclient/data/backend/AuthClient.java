package uk.ac.soton.comp2300.group42.energyclient.data.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.user.AuthResponse;
import uk.ac.soton.comp2300.group42.user.LoginRequest;
import uk.ac.soton.comp2300.group42.user.RegistrationRequest;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Optional;

@Singleton
public class AuthClient {

    private final AuthenticatedHttpClient httpClient;
    private final ObjectMapper mapper;

    @Inject
    public AuthClient(AuthenticatedHttpClient httpClient, ObjectMapper mapper) {
        this.httpClient = httpClient;
        this.mapper = mapper;
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

    public Optional<AuthResponse> login(String email, String password) {
        LoginRequest dto = new LoginRequest(email, password);

        String json;
        try { json = mapper.writeValueAsString(dto); }
        catch (JsonProcessingException e) { throw new RuntimeException("Failed to serialize user for login", e); }

        HttpResponse<String> response;
        try { response = httpClient.post("auth/login", json); }
        catch (IOException e) { throw new RuntimeException("An I/O error occurred when sending or receiving, or the client shut down during login", e); }
        catch (InterruptedException e) { throw new RuntimeException("The API call was interrupted during login", e); }

        if (response.statusCode() != 200)
            return Optional.empty();

        AuthResponse auth;
        try { auth = mapper.readValue(response.body(), AuthResponse.class); }
        catch (JsonProcessingException e) { throw new RuntimeException("Failed to deserialize auth response", e); }

        return Optional.of(auth);
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
        return false;
    }
}
