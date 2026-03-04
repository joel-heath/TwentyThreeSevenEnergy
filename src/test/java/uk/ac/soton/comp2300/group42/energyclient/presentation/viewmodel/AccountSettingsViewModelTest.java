package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.AuthRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHousemate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.UserStore;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountSettingsViewModelTest {

    @Mock private UserStore userStore;
    @Mock private AuthRepository authRepository;
    @Mock private ObservablePreferences preferences;
    @Mock private ObservableHousemate user;

    private AccountSettingsViewModel viewModel;

    @BeforeEach
    void setUp() {
        when(userStore.getPreferences()).thenReturn(preferences);
        when(userStore.getCurrent()).thenReturn(user);
        viewModel = new AccountSettingsViewModel(userStore, authRepository);
    }

    @Test
    void constructor_initialisesObservableFields() {
        assertSame(preferences, viewModel.getPreferences());
        assertSame(user, viewModel.getUser());
    }

    @Test
    void refreshDataAsync_refreshesCurrentUser() {
        viewModel.refreshDataAsync().join();

        verify(userStore).refreshCurrentUser();
    }

    @Test
    void save_delegatesToUserStore() {
        viewModel.save();

        verify(userStore).saveUser();
    }

    @Test
    void logout_logsOutBeforeNavigation() {
        assertThrows(RuntimeException.class, viewModel::logout);

        verify(authRepository).logout();
    }

    @Test
    void deleteAccount_logsOutBeforeNavigation() {
        assertThrows(RuntimeException.class, viewModel::deleteAccount);

        verify(authRepository).logout();
    }
}
