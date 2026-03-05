package uk.ac.soton.comp2300.group42.energyclient.data.backend;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.BackendMapper;
import uk.ac.soton.comp2300.group42.user.AuthResponse;
import uk.ac.soton.comp2300.group42.user.ChangePasswordRequest;
import uk.ac.soton.comp2300.group42.user.LoginRequest;
import uk.ac.soton.comp2300.group42.user.RegistrationRequest;

import java.net.http.HttpResponse;

@Singleton
public class AuthClient extends BaseApiClient {

    @Inject
    public AuthClient(AuthenticatedHttpClient httpClient, @BackendMapper JsonMapper mapper) {
        super(httpClient, mapper);
    }

    public boolean isLoggedIn() {
        return get("users/me").statusCode() == 200;
    }

    public AuthResponse login(LoginRequest request) {
        return post("auth/login", request, new TypeReference<>() {});
    }

    public AuthResponse register(RegistrationRequest request) {
        return post("auth/register", request, new TypeReference<>() {});
    }

    public void changePassword(ChangePasswordRequest request) {
        HttpResponse<String> response = put("users/me/password", request);
        throwIfNotSuccess(response);
    }
}
