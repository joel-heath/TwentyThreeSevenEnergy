package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.AuthRepository;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoginViewModelTest {

    @Mock private AuthRepository repo;

    @Test
    void login_delegatesToRepository() {
        LoginViewModel viewModel = new LoginViewModel(repo);

        viewModel.login("test@example.com", "secret");

        verify(repo).login("test@example.com", "secret");
    }

    @Test
    void register_delegatesToRepository() {
        LoginViewModel viewModel = new LoginViewModel(repo);

        viewModel.register("Name", "test@example.com", "secret");

        verify(repo).register("Name", "test@example.com", "secret");
    }
}
