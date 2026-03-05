package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.backend.AuthClient;
import uk.ac.soton.comp2300.group42.energyclient.data.backend.AuthenticatedHttpClient;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.NetworkException;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.AuthRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;
import uk.ac.soton.comp2300.group42.user.AuthResponse;
import uk.ac.soton.comp2300.group42.user.ChangePasswordRequest;
import uk.ac.soton.comp2300.group42.user.LoginRequest;
import uk.ac.soton.comp2300.group42.user.RegistrationRequest;

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
        try {
            client.isLoggedIn();
            sessionManager.setLoggedIn(true);
            return true;
        }
        catch (NetworkException e) {
            System.out.println("Warning: Network error while verifying login status, either you are offline or are not running the backend.");
            sessionManager.setLoggedIn(false);
            return false;
        }
    }

    @Override
    public void login(String email, String password) {
        AuthResponse response = client.login(new LoginRequest(email, password));
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
        AuthResponse response = client.register(new RegistrationRequest(name, email, password));
        httpClient.setTokenPair(response.accessToken(), response.refreshToken());
        sessionManager.setLoggedIn(true);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        client.changePassword(new ChangePasswordRequest(oldPassword, newPassword));
    }
}
