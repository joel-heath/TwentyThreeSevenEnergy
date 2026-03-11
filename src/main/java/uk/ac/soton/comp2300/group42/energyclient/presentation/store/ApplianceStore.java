package uk.ac.soton.comp2300.group42.energyclient.presentation.store;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.UIExecutor;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.ApplianceRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Singleton
public class ApplianceStore {

    private final ApplianceRepository repository;
    private final HouseStore houseStore;
    private final ObservablePreferences preferences;
    private final Map<Long, ObservableAppliance> cache;
    private final ObservableList<ObservableAppliance> masterList;
    private final Executor uiExecutor;

    @Inject
    public ApplianceStore(ApplianceRepository repository,
                          HouseStore houseStore,
                          ObservablePreferences preferences,
                          SessionManager sessionManager,
                          @UIExecutor Executor uiExecutor) {
        this.repository = repository;
        this.houseStore = houseStore;
        this.preferences = preferences;
        this.uiExecutor = uiExecutor;
        this.cache = new WeakHashMap<>();
        this.masterList = FXCollections.observableArrayList();

        sessionManager.subscribe(_ ->
                uiExecutor.execute(() -> {
                    cache.clear();
                    masterList.clear();
                })
        );
    }

    private Long getActiveHouseId() {
        return preferences.getActiveHouse().getId();
    }

    public ObservableAppliance add(Appliance appliance) {
        Appliance pojo = repository.add(appliance);
        return getObservable(pojo);
    }

    public ObservableAppliance get(Long id) {
        Appliance pojo = repository.get(getActiveHouseId(), id);
        return getObservable(pojo);
    }

    public ObservableList<ObservableAppliance> getAll() {
        return masterList;
    }

    public ObservableAppliance update(Appliance appliance) {
        Appliance pojo = repository.update(appliance);
        return getObservable(pojo);
    }

    public void delete(Long id) {
        repository.delete(getActiveHouseId(), id);
        cache.remove(id);
        masterList.removeIf(app -> Objects.equals(app.getId(), id)); // TODO: uiExecutor.execute when this is called on a background thread
    }

    private ObservableAppliance getObservable(Appliance pojo) {
        ObservableHouse house = houseStore.get(pojo.houseId());
        ObservableAppliance existing = cache.get(pojo.id());

        if (existing != null) {
            uiExecutor.execute(() -> existing.updateFrom(pojo, house));
            return existing;
        } else {
            ObservableAppliance appliance = new ObservableAppliance(pojo, house);
            cache.put(pojo.id(), appliance);
            uiExecutor.execute(() -> masterList.add(appliance));
            return appliance;
        }
    }

    public CompletableFuture<Void> refreshAllAsync() {
        record ApplianceUpdate(ObservableAppliance appliance, Appliance pojo, boolean needsUpdate) {}

        ObservableHouse activeHouse = preferences.getActiveHouse();

        return CompletableFuture.supplyAsync(() ->
            repository.getAll(getActiveHouseId())
            .stream()
            .map(pojo -> {
                ObservableAppliance appliance = cache.get(pojo.id());
                if (appliance != null) {
                    return new ApplianceUpdate(appliance, pojo, true);
                }
                else {
                    appliance = new ObservableAppliance(pojo, activeHouse);
                    cache.put(pojo.id(), appliance);
                    return new ApplianceUpdate(appliance, pojo, false);
                }
            })
            .toList()
        ).thenAcceptAsync(updates -> {
            updates.stream().filter(ApplianceUpdate::needsUpdate).forEach(
                    update -> update.appliance().updateFrom(update.pojo(), activeHouse)
            );

            masterList.setAll(updates.stream().map(ApplianceUpdate::appliance).toList());
        }, uiExecutor);
    }
}