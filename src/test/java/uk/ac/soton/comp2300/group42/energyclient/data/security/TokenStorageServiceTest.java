package uk.ac.soton.comp2300.group42.energyclient.data.security;

import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.Keyring;
import com.github.javakeyring.PasswordAccessException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class TokenStorageServiceTest {

    @Test
    void init_WhenBackendNotSupported_SetsFieldsToNullGracefully() {
        try (MockedStatic<Keyring> mockedKeyring = mockStatic(Keyring.class)) {
            mockedKeyring.when(Keyring::create).thenThrow(new BackendNotSupportedException("No OS keyring found"));

            TokenStorageService service = new TokenStorageService();

            assertDoesNotThrow(() -> service.saveRefreshToken("some-token"));
            assertNull(service.getRefreshToken());
        }
    }

    @Test
    void getRefreshToken_WhenPasswordAccessFails_ReturnsNullGracefully() throws PasswordAccessException {
        Keyring mockKeyringInstance = mock(Keyring.class);

        when(mockKeyringInstance.getPassword(anyString(), anyString()))
                .thenThrow(new PasswordAccessException("Item not found"));

        try (MockedStatic<Keyring> mockedKeyring = mockStatic(Keyring.class)) {
            mockedKeyring.when(Keyring::create).thenReturn(mockKeyringInstance);

            TokenStorageService service = new TokenStorageService();

            String token = service.getRefreshToken();

            assertNull(token);
        }
    }

    @Test
    void saveRefreshToken_WhenPasswordAccessFails_FailsGracefully() throws PasswordAccessException {
        Keyring mockKeyringInstance = mock(Keyring.class);

        doThrow(new PasswordAccessException("Access denied"))
                .when(mockKeyringInstance).setPassword(anyString(), anyString(), anyString());

        try (MockedStatic<Keyring> mockedKeyring = mockStatic(Keyring.class)) {
            mockedKeyring.when(Keyring::create).thenReturn(mockKeyringInstance);

            TokenStorageService service = new TokenStorageService();

            assertDoesNotThrow(() -> service.saveRefreshToken("new-token"));
        }
    }
}