package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.backend.AuthClient;
import uk.ac.soton.comp2300.group42.energyclient.data.backend.AuthenticatedHttpClient;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.AuthRepository;
import uk.ac.soton.comp2300.group42.user.AuthResponse;

import java.util.Optional;

@Singleton
public class RemoteAuthRepository implements AuthRepository {

    private final AuthClient client;
    private final AuthenticatedHttpClient httpClient;

    @Inject
    public RemoteAuthRepository(AuthClient client, AuthenticatedHttpClient httpClient) {
        this.client = client;
        this.httpClient = httpClient;
    }

    @Override
    public boolean isLoggedIn() {
        return client.isLoggedIn();
    }

    @Override
    public boolean login(String email, String password) {
        Optional<AuthResponse> response = client.login(email, password);
        if (response.isPresent()) {
            AuthResponse auth = response.get();
            httpClient.setTokenPair(auth.accessToken(), auth.refreshToken());
            return true;
        }
        return false;
    }

    @Override
    public void logout() {
        httpClient.clearTokenPair();
    }

    @Override
    public boolean register(String name, String email, String password) {
        Optional<AuthResponse> response = client.register(name, email, password);
        if (response.isPresent()) {
            AuthResponse auth = response.get();
            httpClient.setTokenPair(auth.accessToken(), auth.refreshToken());
            return true;
        }
        return false;
    }
}
