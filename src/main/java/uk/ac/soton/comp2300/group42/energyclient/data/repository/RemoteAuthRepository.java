package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.backend.AuthClient;
import uk.ac.soton.comp2300.group42.energyclient.data.backend.AuthenticatedHttpClient;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.NetworkException;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.AuthRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;
import uk.ac.soton.comp2300.group42.user.AuthResponse;

@Singleton
public class RemoteAuthRepository implements AuthRepository {

    private final AuthClient client;
    private final AuthenticatedHttpClient httpClient;
    private final SessionManager sessionManager;

    @Inject
    public RemoteAuthRepository(AuthClient client, AuthenticatedHttpClient httpClient, SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.client = client;
        this.httpClient = httpClient;
    }

    @Override
    public boolean verifyLoggedIn() {
        boolean loggedIn;
        try {
            loggedIn = client.isLoggedIn();
        }
        catch (NetworkException e) {
            System.out.println("Warning: Network error while verifying login status, either you are offline or are not running the backend.");
            loggedIn = false;
        }
        sessionManager.setLoggedIn(loggedIn);
        return loggedIn;
    }

    @Override
    public void login(String email, String password) {
        AuthResponse response = client.login(email, password);
        httpClient.setTokenPair(response.accessToken(), response.refreshToken());
        sessionManager.setLoggedIn(true);
    }

    @Override
    public void logout() {
        httpClient.clearTokenPair();
        sessionManager.setLoggedIn(false);
    }

    @Override
    public void register(String name, String email, String password) {
        AuthResponse response = client.register(name, email, password);
        httpClient.setTokenPair(response.accessToken(), response.refreshToken());
        sessionManager.setLoggedIn(true);
    }
}
