package uk.ac.soton.comp2300.group42.energyclient.presentation.store;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Activation;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.ActivationRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableActivation;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@Singleton
public class ActivationStore {

    private final ActivationRepository repository;
    private final ApplianceStore applianceStore;
    private final ObservablePreferences preferences;
    private final Map<Long, ObservableActivation> cache;
    private final ObservableList<ObservableActivation> masterList;

    @Inject
    public ActivationStore(ActivationRepository repository, ApplianceStore applianceStore, ObservablePreferences preferences) {
        this.repository = repository;
        this.applianceStore = applianceStore;
        this.preferences = preferences;
        this.cache = new WeakHashMap<>();
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
        Activation pojo = repository.get(id, getActiveHouseId());
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
    }

    private ObservableActivation getObservable(Activation pojo) {
        ObservableAppliance appliance = applianceStore.get(pojo.applianceId());
        ObservableActivation existing = cache.get(pojo.id());

        if (existing != null) {
            Platform.runLater(() -> existing.updateFrom(pojo, appliance));
            return existing;
        } else {
            ObservableActivation activation = new ObservableActivation(pojo, appliance);
            cache.put(pojo.id(), activation);
            Platform.runLater(() -> masterList.add(activation));
            return activation;
        }
    }

    public void refreshAll() {
        List<Activation> pojos = repository.getAll(getActiveHouseId());
        applianceStore.refreshAll();

        Platform.runLater(() -> {
            masterList.clear();
            for (Activation pojo : pojos) {
                ObservableActivation activation = cache.get(pojo.id());
                ObservableAppliance appliance = applianceStore.get(pojo.applianceId());
                if (activation != null) {
                    activation.updateFrom(pojo, appliance);
                }
                else {
                    activation = new ObservableActivation(pojo, appliance);
                    cache.put(pojo.id(), activation);
                }
                masterList.add(activation);
            }
        });
    }
}