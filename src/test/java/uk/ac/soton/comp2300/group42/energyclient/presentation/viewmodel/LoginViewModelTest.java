package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.ApiException;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.AuthRepository;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoginViewModelTest {

    @Mock private AuthRepository authRepository;

    private LoginViewModel viewModel;

    @BeforeEach
    void setUp() {
        viewModel = new LoginViewModel(authRepository);
    }

    @Test
    void login_whenEmailIsBlank_setsValidationError() {
        viewModel.emailProperty().set(" ");
        viewModel.passwordProperty().set("pass");

        boolean result = viewModel.login();

        assertFalse(result);
        assertTrue(viewModel.hasResponseErrorProperty().get());
        assertEquals("Email is required", viewModel.responseMessageProperty().get());
    }

    @Test
    void login_whenPasswordIsBlank_setsValidationError() {
        viewModel.emailProperty().set("user@example.com");
        viewModel.passwordProperty().set(" ");

        boolean result = viewModel.login();

        assertFalse(result);
        assertTrue(viewModel.hasResponseErrorProperty().get());
        assertEquals("Password is required", viewModel.responseMessageProperty().get());
    }

    @Test
    void login_whenRepositoryThrows_setsApiError() {
        viewModel.emailProperty().set("user@example.com");
        viewModel.passwordProperty().set("Secret123!");
        doThrow(apiException("Invalid credentials")).when(authRepository).login("user@example.com", "Secret123!");

        boolean result = viewModel.login();

        assertFalse(result);
        assertTrue(viewModel.hasResponseErrorProperty().get());
        assertEquals("Invalid credentials", viewModel.responseMessageProperty().get());
    }

    @Test
    void login_withValidInputs_callsRepositoryAndReturnsTrue() {
        viewModel.emailProperty().set("user@example.com");
        viewModel.passwordProperty().set("Secret123!");

        boolean result = viewModel.login();

        assertTrue(result);
        assertFalse(viewModel.hasResponseErrorProperty().get());
        verify(authRepository).login("user@example.com", "Secret123!");
    }

    private static ApiException apiException(String message) {
        return new ApiException(Instant.now(), 400, "Bad Request", message, "/auth/login");
    }
}

