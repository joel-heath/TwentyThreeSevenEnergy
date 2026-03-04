package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.AuthRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginViewModelTest {

    @Mock private AuthRepository repo;

    @Test
    void login_delegatesToRepository() {
        when(repo.login("test@example.com", "secret")).thenReturn(true);
        LoginViewModel viewModel = new LoginViewModel(repo);

        boolean result = viewModel.login("test@example.com", "secret");

        assertTrue(result);
        verify(repo).login("test@example.com", "secret");
    }

    @Test
    void register_delegatesToRepository() {
        when(repo.register("Name", "test@example.com", "secret")).thenReturn(false);
        LoginViewModel viewModel = new LoginViewModel(repo);

        boolean result = viewModel.register("Name", "test@example.com", "secret");

        assertFalse(result);
        verify(repo).register("Name", "test@example.com", "secret");
    }
}
