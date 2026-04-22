package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.ApiException;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.BadRequestException;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.AuthRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHousemate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.UserStore;

import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AccountSettingsViewModelTest {

    @Mock private UserStore userStore;
    @Mock private AuthRepository authRepository;

    private ObservableHousemate currentUser;
    private AccountSettingsViewModel viewModel;

    @BeforeEach
    void setUp() {
        ObservableHouse house = new ObservableHouse(new House(1L, "Home", "1 Street", ZoneId.of("UTC"), Role.OWNER));
        currentUser = new ObservableHousemate(new Housemate(1L, 1L, "Alice", "alice@example.com", Role.OWNER), house);

        when(userStore.getCurrent()).thenReturn(currentUser);
        when(userStore.refreshAsync()).thenReturn(CompletableFuture.completedFuture(null));

        viewModel = new AccountSettingsViewModel(userStore, authRepository, Runnable::run);
    }

    @Test
    void loadData_success_populatesFieldsAndClearsLoading() {
        viewModel.loadData();

        assertEquals("Alice", viewModel.nameProperty().get());
        assertEquals("alice@example.com", viewModel.emailProperty().get());
        assertFalse(viewModel.isLoadingProperty().get());
    }

    @Test
    void loadData_failure_setsErrorAndClearsFields() {
        when(userStore.refreshAsync()).thenReturn(CompletableFuture.failedFuture(new RuntimeException("boom")));

        viewModel.loadData();

        assertEquals("", viewModel.nameProperty().get());
        assertEquals("", viewModel.emailProperty().get());
        assertEquals("Failed to load user data.", viewModel.responseMessageProperty().get());
        assertEquals("response-error", viewModel.responseStyleClassProperty().get());
        assertFalse(viewModel.isLoadingProperty().get());
    }

    @Test
    void updateName_whenBlank_setsValidationError() {
        viewModel.nameProperty().set("   ");

        viewModel.updateName();

        assertEquals("Name cannot be empty.", viewModel.responseMessageProperty().get());
        assertEquals("response-error", viewModel.responseStyleClassProperty().get());
        verify(userStore, never()).saveUser();
    }

    @Test
    void updateName_whenValid_trimsAndPersists() {
        viewModel.nameProperty().set("  Bob  ");

        viewModel.updateName();

        assertEquals("Bob", currentUser.getName());
        assertEquals("Name updated successfully.", viewModel.responseMessageProperty().get());
        assertEquals("response-success", viewModel.responseStyleClassProperty().get());
        verify(userStore).saveUser();
    }

    @Test
    void updateName_whenRepositoryFails_setsError() {
        doThrow(apiException("save failed")).when(userStore).saveUser();
        viewModel.nameProperty().set("Bob");

        viewModel.updateName();

        assertEquals("Failed to update name: save failed", viewModel.responseMessageProperty().get());
        assertEquals("response-error", viewModel.responseStyleClassProperty().get());
    }

    @Test
    void updateEmail_whenInvalid_setsValidationError() {
        viewModel.emailProperty().set("invalid-email");

        viewModel.updateEmail();

        assertEquals("Invalid email format.", viewModel.responseMessageProperty().get());
        assertEquals("response-error", viewModel.responseStyleClassProperty().get());
        verify(userStore, never()).saveUser();
    }

    @Test
    void updateEmail_whenValid_trimsAndPersists() {
        viewModel.emailProperty().set("bob@example.com");

        viewModel.updateEmail();

        assertEquals("bob@example.com", currentUser.getEmail());
        assertEquals("Email updated successfully.", viewModel.responseMessageProperty().get());
        assertEquals("response-success", viewModel.responseStyleClassProperty().get());
        verify(userStore).saveUser();
    }

    @Test
    void updatePassword_whenMismatched_setsValidationError() {
        viewModel.currentPasswordProperty().set("old");
        viewModel.newPasswordProperty().set("NewPass1!");
        viewModel.confirmPasswordProperty().set("Different1!");

        viewModel.updatePassword();

        assertEquals("Passwords do not match.", viewModel.responseMessageProperty().get());
        assertEquals("response-error", viewModel.responseStyleClassProperty().get());
        verify(authRepository, never()).changePassword("old", "NewPass1!");
    }

    @Test
    void updatePassword_whenBadRequestHasValidationPrefix_extractsMessage() {
        doThrow(new BadRequestException(Instant.now(), "Bad Request", "Validation failed: newPassword: too weak", "/auth/password"))
                .when(authRepository).changePassword("old", "short");
        viewModel.currentPasswordProperty().set("old");
        viewModel.newPasswordProperty().set("short");
        viewModel.confirmPasswordProperty().set("short");

        viewModel.updatePassword();

        assertEquals("too weak", viewModel.responseMessageProperty().get());
        assertEquals("response-error", viewModel.responseStyleClassProperty().get());
    }

    @Test
    void updatePassword_whenValid_callsRepositoryAndClearsInputs() {
        viewModel.currentPasswordProperty().set("old");
        viewModel.newPasswordProperty().set("NewPass1!");
        viewModel.confirmPasswordProperty().set("NewPass1!");

        viewModel.updatePassword();

        verify(authRepository).changePassword("old", "NewPass1!");
        assertEquals("", viewModel.currentPasswordProperty().get());
        assertEquals("", viewModel.newPasswordProperty().get());
        assertEquals("", viewModel.confirmPasswordProperty().get());
        assertEquals("Account updated successfully.", viewModel.responseMessageProperty().get());
        assertEquals("response-success", viewModel.responseStyleClassProperty().get());
    }

    @Test
    void deleteAccount_whenPasswordMissing_returnsFalse() {
        viewModel.currentPasswordProperty().set("");

        boolean result = viewModel.deleteAccount();

        assertFalse(result);
        assertEquals("Must enter password to delete account.", viewModel.responseMessageProperty().get());
        assertEquals("response-error", viewModel.responseStyleClassProperty().get());
        verify(userStore, never()).deleteUser("");
        verify(authRepository, never()).logout();
    }

    @Test
    void deleteAccount_whenRepositoryFails_returnsFalse() {
        doThrow(apiException("wrong password")).when(userStore).deleteUser("bad-password");
        viewModel.currentPasswordProperty().set("bad-password");

        boolean result = viewModel.deleteAccount();

        assertFalse(result);
        assertEquals("Failed to delete account: wrong password", viewModel.responseMessageProperty().get());
        assertEquals("response-error", viewModel.responseStyleClassProperty().get());
        verify(authRepository, never()).logout();
    }

    @Test
    void deleteAccount_whenSuccessful_deletesUserAndLogsOut() {
        viewModel.currentPasswordProperty().set("good-password");

        boolean result = viewModel.deleteAccount();

        assertTrue(result);
        verify(userStore).deleteUser("good-password");
        verify(authRepository).logout();
    }

    @Test
    void logout_delegatesToRepository() {
        viewModel.logout();
        verify(authRepository).logout();
    }

    private static ApiException apiException(String message) {
        return new ApiException(Instant.now(), 400, "Bad Request", message, "/account");
    }
}
