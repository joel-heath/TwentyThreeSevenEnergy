package uk.ac.soton.comp2300.group42.energyclient.data.backend;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.BackendMapper;
import uk.ac.soton.comp2300.group42.user.AuthResponse;
import uk.ac.soton.comp2300.group42.user.LoginRequest;
import uk.ac.soton.comp2300.group42.user.RegistrationRequest;

import java.net.http.HttpResponse;
import java.util.Optional;

@Singleton
public class AuthClient extends BaseApiClient {

    @Inject
    public AuthClient(AuthenticatedHttpClient httpClient, @BackendMapper JsonMapper mapper) {
        super(httpClient, mapper);
    }

    public boolean isLoggedIn() {
        return get("users/me").statusCode() == 200;
    }

    public Optional<AuthResponse> login(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);

        HttpResponse<String> response = post("auth/login", request);
        if (!isSuccess(response))
            return Optional.empty();

        AuthResponse auth = handleResponse(response, new TypeReference<>() {});
        return Optional.of(auth);
    }

    public Optional<AuthResponse> register(String name, String email, String password) {
        RegistrationRequest request = new RegistrationRequest(name, email, password);

        HttpResponse<String> response = post("auth/register", request);
        if (!isSuccess(response))
            return Optional.empty();

        AuthResponse auth = handleResponse(response, new TypeReference<>() {});
        return Optional.of(auth);
    }
}
