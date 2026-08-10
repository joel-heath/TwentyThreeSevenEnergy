package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.data.backend.AuthClient;
import uk.ac.soton.comp2300.group42.energyclient.data.backend.AuthenticatedHttpClient;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.NetworkException;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;
import uk.ac.soton.comp2300.group42.user.AuthResponse;
import uk.ac.soton.comp2300.group42.user.ChangePasswordRequest;
import uk.ac.soton.comp2300.group42.user.LoginRequest;
import uk.ac.soton.comp2300.group42.user.RegistrationRequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoteAuthRepositoryTest {

    @Mock private AuthClient client;
    @Mock private AuthenticatedHttpClient httpClient;
    @Mock private SessionManager sessionManager;

    @InjectMocks private RemoteAuthRepository repository;

    @Test
    void verifyLoggedIn_whenClientReturnsTrue_returnsTrue() {
        when(client.isLoggedIn()).thenReturn(true);

        boolean result = repository.verifyLoggedIn();

        assertTrue(result);
        verify(client).isLoggedIn();
    }

    @Test
    void verifyLoggedIn_whenNetworkExceptionOccurs_returnsFalse() {
        when(client.isLoggedIn()).thenThrow(new NetworkException("offline"));

        boolean result = repository.verifyLoggedIn();

        assertFalse(result);
        verify(client).isLoggedIn();
    }

    @Test
    void login_setsTokensAndMarksSessionAsLoggedIn() {
        when(client.login(any(LoginRequest.class)))
                .thenReturn(new AuthResponse("access", "refresh"));

        repository.login("a@test.com", "pw");

        verify(client).login(any(LoginRequest.class));
        verify(httpClient).setTokenPair("access", "refresh");
        verify(sessionManager).setLoggedIn(true);
    }

    @Test
    void register_setsTokensAndMarksSessionAsLoggedIn() {
        when(client.register(any(RegistrationRequest.class)))
                .thenReturn(new AuthResponse("access2", "refresh2"));

        repository.register("Alice", "a@test.com", "pw");

        verify(client).register(any(RegistrationRequest.class));
        verify(httpClient).setTokenPair("access2", "refresh2");
        verify(sessionManager).setLoggedIn(true);
    }

    @Test
    void logout_clearsTokensAndMarksSessionAsLoggedOut() {
        repository.logout();

        verify(httpClient).clearTokenPair();
        verify(sessionManager).setLoggedIn(false);
        verifyNoInteractions(client);
    }

    @Test
    void changePassword_delegatesToClient() {
        repository.changePassword("oldPw", "newPw");

        verify(client).changePassword(any(ChangePasswordRequest.class));
        verifyNoInteractions(httpClient, sessionManager);
    }
}
