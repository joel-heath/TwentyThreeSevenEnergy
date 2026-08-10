package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.ApiException;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.AuthRepository;
import uk.ac.soton.comp2300.group42.user.PasswordValidation;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegisterViewModelTest {

    @Mock private AuthRepository authRepository;

    private RegisterViewModel viewModel;

    @BeforeEach
    void setUp() {
        viewModel = new RegisterViewModel(authRepository);
    }

    @Test
    void register_whenFieldsMissing_setsValidationError() {
        viewModel.nameProperty().set(" ");
        viewModel.emailProperty().set("user@example.com");
        viewModel.passwordProperty().set("StrongPass1!");
        viewModel.confirmPasswordProperty().set("StrongPass1!");

        boolean result = viewModel.register();

        assertFalse(result);
        assertTrue(viewModel.hasResponseErrorProperty().get());
        assertEquals("Name cannot be blank", viewModel.responseMessageProperty().get());
    }

    @Test
    void register_whenPasswordsDoNotMatch_setsError() {
        viewModel.nameProperty().set("User");
        viewModel.emailProperty().set("user@example.com");
        viewModel.passwordProperty().set("StrongPass1!");
        viewModel.confirmPasswordProperty().set("Different1!");

        boolean result = viewModel.register();

        assertFalse(result);
        assertEquals("Passwords do not match.", viewModel.responseMessageProperty().get());
    }

    @Test
    void register_whenPasswordInsecure_setsDetailedError() {
        viewModel.nameProperty().set("User");
        viewModel.emailProperty().set("user@example.com");
        viewModel.passwordProperty().set("abc");
        viewModel.confirmPasswordProperty().set("abc");

        boolean result = viewModel.register();

        assertFalse(result);
        assertTrue(viewModel.responseMessageProperty().get().startsWith("Password is insecure:"));
    }

    @Test
    void register_whenPasswordMissingLowercase_reportsSpecificRequirement() {
        viewModel.nameProperty().set("User");
        viewModel.emailProperty().set("user@example.com");
        viewModel.passwordProperty().set("ABCDEFG!");
        viewModel.confirmPasswordProperty().set("ABCDEFG!");

        boolean result = viewModel.register();

        assertFalse(result);
        assertEquals("Password is insecure: missing one lowercase letter.", viewModel.responseMessageProperty().get());
    }

    @Test
    void register_whenRepositoryThrows_setsApiError() {
        viewModel.nameProperty().set("User");
        viewModel.emailProperty().set("user@example.com");
        viewModel.passwordProperty().set("StrongPass1!");
        viewModel.confirmPasswordProperty().set("StrongPass1!");
        doThrow(apiException("Email already exists")).when(authRepository).register("User", "user@example.com", "StrongPass1!");

        boolean result = viewModel.register();

        assertFalse(result);
        assertTrue(viewModel.hasResponseErrorProperty().get());
        assertEquals("Email already exists", viewModel.responseMessageProperty().get());
    }

    @Test
    void register_withValidInputs_callsRepositoryAndReturnsTrue() {
        viewModel.nameProperty().set("User");
        viewModel.emailProperty().set("user@example.com");
        viewModel.passwordProperty().set("StrongPass1!");
        viewModel.confirmPasswordProperty().set("StrongPass1!");

        boolean result = viewModel.register();

        assertTrue(result);
        verify(authRepository).register("User", "user@example.com", "StrongPass1!");
    }

    @Test
    void buildPasswordInsecurityMessage_whenNoRequirementsMissing_returnsContractMessage() throws Exception {
        Method method = RegisterViewModel.class.getDeclaredMethod("buildPasswordInsecurityMessage", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(null, "Abcdefg!");

        assertEquals(PasswordValidation.PASSWORD_QUALITY_MESSAGE, result);
    }

    @Test
    void formatMissingRequirements_withTwoEntries_usesAndJoin() throws Exception {
        Method method = RegisterViewModel.class.getDeclaredMethod("formatMissingRequirements", List.class);
        method.setAccessible(true);

        String result = (String) method.invoke(null, List.of("one uppercase letter", "one special character"));

        assertEquals("one uppercase letter and one special character", result);
    }

    private static ApiException apiException(String message) {
        return new ApiException(Instant.now(), 400, "Bad Request", message, "/auth/register");
    }
}
