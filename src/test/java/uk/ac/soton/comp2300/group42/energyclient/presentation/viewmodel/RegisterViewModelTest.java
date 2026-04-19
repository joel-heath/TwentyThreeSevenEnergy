package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.AuthRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class RegisterViewModelTest {

    @Mock private AuthRepository repo;

    @Test
    void register_withSecurePassword_delegatesToRepository() {
        RegisterViewModel viewModel = new RegisterViewModel(repo);
        viewModel.nameProperty().set("Alice");
        viewModel.emailProperty().set("alice@test.com");
        viewModel.passwordProperty().set("ValidPass!");
        viewModel.confirmPasswordProperty().set("ValidPass!");

        boolean result = viewModel.register();

        assertTrue(result);
        verify(repo).register("Alice", "alice@test.com", "ValidPass!");
    }

    @Test
    void register_withInsecurePassword_showsSpecificReason() {
        RegisterViewModel viewModel = new RegisterViewModel(repo);
        viewModel.nameProperty().set("Alice");
        viewModel.emailProperty().set("alice@test.com");
        viewModel.passwordProperty().set("lowercase!");
        viewModel.confirmPasswordProperty().set("lowercase!");

        boolean result = viewModel.register();

        assertFalse(result);
        assertTrue(viewModel.responseMessageProperty().get().contains("one uppercase letter"));
        verifyNoInteractions(repo);
    }

    @Test
    void register_withMultipleMissingRequirements_showsAllMissingRules() {
        RegisterViewModel viewModel = new RegisterViewModel(repo);
        viewModel.nameProperty().set("Alice");
        viewModel.emailProperty().set("alice@test.com");
        viewModel.passwordProperty().set("abc");
        viewModel.confirmPasswordProperty().set("abc");

        boolean result = viewModel.register();

        assertFalse(result);
        assertTrue(viewModel.responseMessageProperty().get().contains("at least 8 characters"));
        assertTrue(viewModel.responseMessageProperty().get().contains("one uppercase letter"));
        assertTrue(viewModel.responseMessageProperty().get().contains("one special character"));
        verifyNoInteractions(repo);
    }
}
