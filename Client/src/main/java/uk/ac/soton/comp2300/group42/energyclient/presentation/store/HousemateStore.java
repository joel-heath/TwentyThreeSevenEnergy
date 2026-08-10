package uk.ac.soton.comp2300.group42.energyclient.presentation.store;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.UIExecutor;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.HouseRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHousemate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Singleton
public class HousemateStore {

    private final HouseRepository repository;
    private final HouseStore houseStore;
    private final ObservablePreferences preferences;
    private final Map<Long, ObservableHousemate> cache;
    private final ObservableList<ObservableHousemate> masterList;
    private final Executor uiExecutor;

    @Inject
    public HousemateStore(HouseRepository repository,
                          HouseStore houseStore,
                          ObservablePreferences preferences,
                          @UIExecutor Executor uiExecutor) {
        this.repository = repository;
        this.houseStore = houseStore;
        this.preferences = preferences;
        this.uiExecutor = uiExecutor;
        this.cache = new HashMap<>();
        this.masterList = FXCollections.observableArrayList();
    }

    private Long getActiveHouseId() {
        return preferences.getActiveHouse().getId();
    }

    public ObservableHousemate invite(Housemate housemate) {
        // TODO: repository.inviteHousemate(getActiveHouseId(), housemate)
        return getObservable(housemate);
    }

    public void kick(Long userId) {
        // repository.kickHousemate(getActiveHouseId(), userId);
        cache.remove(userId);
        uiExecutor.execute(() -> masterList.removeIf(h -> h.getId().equals(userId)));
    }

    public ObservableHousemate get(Long userId) {
        // Housemate pojo = repository.getHousemate(getActiveHouseId(), userId);
        // return getObservable(pojo);
        throw new UnsupportedOperationException();
    }

    public ObservableHousemate update(Housemate housemate) {
        //Housemate pojo = repository.updateHousemate(getActiveHouseId(), housemate);
        //return getObservable(pojo);
        throw new UnsupportedOperationException();
    }

    private ObservableHousemate getObservable(Housemate pojo) {
        ObservableHouse house = houseStore.get(pojo.houseId());
        ObservableHousemate existing = cache.get(pojo.userId());

        if (existing != null) {
            uiExecutor.execute(() -> existing.updateFrom(pojo, house));
            return existing;
        } else {
            ObservableHousemate housemate = new ObservableHousemate(pojo, house);
            cache.put(pojo.userId(), housemate);
            uiExecutor.execute(() -> masterList.add(housemate));
            return housemate;
        }
    }


    public ObservableList<ObservableHousemate> getAll() {
        return masterList;
    }

    public CompletableFuture<Void> refreshAllAsync() {
        record HousemateUpdate(ObservableHousemate housemate, Housemate pojo, boolean needsUpdate) {}

        ObservableHouse activeHouse = preferences.getActiveHouse();

        return CompletableFuture.supplyAsync(() ->
            repository.getHousemates(getActiveHouseId())
            .stream()
            .map(pojo -> {
                ObservableHousemate housemate = cache.get(pojo.userId());
                if (housemate != null) {
                    return new HousemateUpdate(housemate, pojo, true);
                }
                else {
                    housemate = new ObservableHousemate(pojo, activeHouse);
                    cache.put(pojo.userId(), housemate);
                    return new HousemateUpdate(housemate, pojo, false);
                }
            })
            .toList()
        ).thenAcceptAsync(updates -> {
            updates.stream().filter(HousemateUpdate::needsUpdate).forEach(
                    update -> update.housemate().updateFrom(update.pojo(), activeHouse)
            );

            masterList.setAll(updates.stream().map(HousemateUpdate::housemate).toList());
        }, uiExecutor);
    }

    public CompletableFuture<Void> invalidateCacheAsync() {
        return CompletableFuture.runAsync(() -> {
            cache.clear();
            masterList.clear();
        }, uiExecutor);
    }
}