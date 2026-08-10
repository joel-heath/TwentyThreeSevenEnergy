package uk.ac.soton.comp2300.group42.energyclient.presentation.store;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.UIExecutor;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Activation;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.ActivationRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableActivation;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Singleton
public class ActivationStore {

    private final ActivationRepository repository;
    private final ApplianceStore applianceStore;
    private final ObservablePreferences preferences;
    private final Map<Long, ObservableActivation> cache;
    private final ObservableList<ObservableActivation> masterList;
    private final Executor uiExecutor;

    @Inject
    public ActivationStore(ActivationRepository repository,
                           ApplianceStore applianceStore,
                           ObservablePreferences preferences,
                           @UIExecutor Executor uiExecutor) {
        this.repository = repository;
        this.applianceStore = applianceStore;
        this.preferences = preferences;
        this.uiExecutor = uiExecutor;
        this.cache = new HashMap<>();
        this.masterList = FXCollections.observableArrayList(
                a -> new Observable[] {
                        a.activationTimeProperty(),
                        a.activationDateProperty(),
                        a.activationTypeProperty(),
                        a.recursMondayProperty(),
                        a.recursTuesdayProperty(),
                        a.recursWednesdayProperty(),
                        a.recursThursdayProperty(),
                        a.recursFridayProperty(),
                        a.recursSaturdayProperty(),
                        a.recursSundayProperty(),
                        a.updateTriggerProperty()
                }
        );
    }

    private Long getActiveHouseId() {
        return preferences.getActiveHouse().getId();
    }

    public ObservableActivation add(Activation activation) {
        Activation pojo = repository.add(activation);
        return getObservable(pojo);
    }

    public ObservableActivation get(Long id) {
        Activation pojo = repository.get(getActiveHouseId(), id);
        return getObservable(pojo);
    }

    public ObservableList<ObservableActivation> getAll() {
        return masterList;
    }

    public ObservableActivation update(Activation activation) {
        Activation pojo = repository.update(activation);
        return getObservable(pojo);
    }

    public void delete(Long id) {
        repository.delete(getActiveHouseId(), id);
        cache.remove(id);
        masterList.removeIf(act -> Objects.equals(act.getId(), id));
    }

    private ObservableActivation getObservable(Activation pojo) {
        ObservableAppliance appliance = applianceStore.get(pojo.applianceId());
        ObservableActivation existing = cache.get(pojo.id());

        if (existing != null) {
            uiExecutor.execute(() -> existing.updateFrom(pojo, appliance));
            return existing;
        } else {
            ObservableActivation activation = new ObservableActivation(pojo, appliance);
            cache.put(pojo.id(), activation);
            uiExecutor.execute(() -> masterList.add(activation));
            return activation;
        }
    }

    public CompletableFuture<Void> refreshAllAsync() {
        record ActivationUpdate(ObservableActivation activation, ObservableAppliance appliance, Activation pojo, boolean needsUpdate) {}

        return CompletableFuture.supplyAsync(() ->
            repository.getAll(getActiveHouseId())
            .stream()
            .map(pojo -> {
                ObservableActivation activation = cache.get(pojo.id());
                ObservableAppliance appliance = applianceStore.get(pojo.applianceId());

                if (activation != null) {
                    return new ActivationUpdate(activation, appliance, pojo, true);
                } else {
                    activation = new ObservableActivation(pojo, appliance);
                    cache.put(pojo.id(), activation);
                    return new ActivationUpdate(activation, appliance, pojo, false);
                }
            })
            .toList()
        ).thenAcceptAsync(updates -> {
            updates.stream().filter(ActivationUpdate::needsUpdate).forEach(
                    update -> update.activation().updateFrom(update.pojo(), update.appliance())
            );

            masterList.setAll(updates.stream().map(ActivationUpdate::activation).toList());
        }, uiExecutor);
    }

    public CompletableFuture<Void> invalidateCacheAsync() {
        return CompletableFuture.runAsync(() -> {
            cache.clear();
            masterList.clear();
        }, uiExecutor);
    }
}