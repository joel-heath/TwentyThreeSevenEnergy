package uk.ac.soton.comp2300.group42.energyclient.presentation.store;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.UIExecutor;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.User;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.HouseRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.UserRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.ColorVisionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHousemate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;

import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Singleton
public class UserStore {

    private final UserRepository userRepo;
    private final HouseRepository houseRepo;
    private final HouseStore houseStore;
    private final ObservableHousemate currentUser;
    private final ObservablePreferences preferences;
    private final Executor uiExecutor;

    @Inject
    public UserStore(UserRepository userRepo,
                     HouseRepository houseRepo,
                     HouseStore houseStore,
                     SessionManager sessionManager,
                     @UIExecutor Executor uiExecutor) {
        this.userRepo = userRepo;
        this.houseRepo = houseRepo;
        this.houseStore = houseStore;
        this.uiExecutor = uiExecutor;

        final var tmpHouse = new ObservableHouse(new House(-1L, "Loading...", "Loading...", ZoneId.systemDefault(), Role.GUEST));
        final var tmpHousemate = new Housemate(-1L, -1L, "Loading...", "Loading...", Role.GUEST);
        this.currentUser = new ObservableHousemate(tmpHousemate, tmpHouse);
        this.preferences = new ObservablePreferences(new Preferences(), tmpHouse);
        ColorVisionManager.bind(preferences.visionProperty());

        preferences.activeHouseProperty().subscribe((_, newHouse) -> {
            if (newHouse == null) return;
            Housemate me = houseRepo.getCurrentUserAsHousemate(newHouse.getId());
            uiExecutor.execute(() -> currentUser.updateFrom(me, newHouse));
        });
        currentUser.houseProperty().subscribe((_, newHouse) -> {
            if (newHouse == null || !newHouse.equals(preferences.getActiveHouse())) return;
            uiExecutor.execute(() -> preferences.setActiveHouse(newHouse));
        });

        // We choose to run this once on the main thread so that
        // all other stores are guaranteed to have the correct user and preferences
        refresh();

        sessionManager.subscribe(_ ->
            uiExecutor.execute(() -> {
                currentUser.updateFrom(tmpHousemate, tmpHouse);
                refresh();
            }), false
        );
    }

    public ObservableHousemate getCurrent() { return currentUser; }
    public ObservablePreferences getPreferences() { return preferences; }

    public void savePreferences() {
        userRepo.updateCurrentPreferences(preferences.commit(currentUser.getId()));
    }

    public void saveUser() {
        Housemate me = currentUser.commit();
        User user = new User(me.userId(), me.name(), me.email());

        userRepo.updateMe(user);
    }

    public void deleteUser(String password) {
        userRepo.deleteMe(password);
        // TODO: need to run many resetting operations that are currently inside constructors ...
    }

    public CompletableFuture<Void> refreshAsync() {
        record Data(Preferences prefs, ObservableHouse house, Housemate me) {}

        return CompletableFuture.supplyAsync(() -> {
            var prefs = userRepo.getCurrentPreferences();
            var house = houseStore.get(prefs.activeHouseId());
            var me = houseRepo.getCurrentUserAsHousemate(house.getId());

            return new Data(prefs, house, me);
        }).thenAcceptAsync(data -> {
            preferences.updateFrom(data.prefs(), data.house());
            currentUser.updateFrom(data.me(), data.house());
        }, uiExecutor);
    }

    public void refresh() {
        var prefs = userRepo.getCurrentPreferences();
        var house = houseStore.get(prefs.activeHouseId());
        var me = houseRepo.getCurrentUserAsHousemate(house.getId());
        preferences.updateFrom(prefs, house);
        currentUser.updateFrom(me, house);
    }

    // TODO: login/out, which calls AuthRepo and updates the current user and preferences accordingly
}
