package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.AuthRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHousemate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.UserStore;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;

import java.util.concurrent.CompletableFuture;

public class AccountSettingsViewModel {

    private final UserStore userStore;
    private final AuthRepository authRepo;
    private final ObservablePreferences preferences;
    private final ObservableHousemate user;

    @Inject public AccountSettingsViewModel(UserStore userStore, AuthRepository authRepo) {
        this.userStore = userStore;
        this.authRepo = authRepo;
        this.preferences = userStore.getPreferences();
        this.user = userStore.getCurrent();
    }

    public CompletableFuture<Void> refreshDataAsync() {
        return CompletableFuture.runAsync(userStore::refresh);
    }

    public ObservablePreferences getPreferences() {
        return preferences;
    }

    public ObservableHousemate getUser() {
        return user;
    }

    public void logout() {
        authRepo.logout();
        Navigator.goToIrreversible("Dashboard.fxml");
    }

    public void save() {
        userStore.saveUser();
    }

    public void deleteAccount(String password) {
        userStore.deleteUser(password);
        authRepo.logout();
        Navigator.goToIrreversible("Landing.fxml");
    }

    public void setPassword(String oldPassword, String newPassword) {
        authRepo.changePassword(oldPassword, newPassword);
    }
}
