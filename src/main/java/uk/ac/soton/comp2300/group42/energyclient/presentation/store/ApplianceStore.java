package uk.ac.soton.comp2300.group42.energyclient.presentation.store;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.ApplianceRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@Singleton
public class ApplianceStore {

    private final ApplianceRepository repository;
    private final HouseStore houseStore;
    private final ObservablePreferences preferences;
    private final Map<Long, ObservableAppliance> cache;
    private final ObservableList<ObservableAppliance> masterList;

    @Inject
    public ApplianceStore(ApplianceRepository repository, HouseStore houseStore, ObservablePreferences preferences) {
        this.repository = repository;
        this.houseStore = houseStore;
        this.preferences = preferences;
        this.cache = new WeakHashMap<>();
        this.masterList = FXCollections.observableArrayList();
    }

    private Long getActiveHouseId() {
        return preferences.getActiveHouse().getId();
    }

    public ObservableAppliance add(Appliance appliance) {
        Appliance pojo = repository.add(appliance);
        return getObservable(pojo);
    }

    public ObservableAppliance get(Long id) {
        Appliance pojo = repository.get(id, getActiveHouseId());
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
    }

    private ObservableAppliance getObservable(Appliance pojo) {
        ObservableHouse house = houseStore.get(pojo.houseId());
        ObservableAppliance existing = cache.get(pojo.id());

        if (existing != null) {
            Platform.runLater(() -> existing.updateFrom(pojo, house));
            return existing;
        } else {
            ObservableAppliance appliance = new ObservableAppliance(pojo, house);
            cache.put(pojo.id(), appliance);
            Platform.runLater(() -> masterList.add(appliance));
            return appliance;
        }
    }

    public void refreshAll() {
        ObservableHouse activeHouse = preferences.getActiveHouse();
        List<Appliance> pojos = repository.getAll(getActiveHouseId());

        Platform.runLater(() -> {
            masterList.clear();
            for (Appliance pojo : pojos) {
                ObservableAppliance appliance = cache.get(pojo.id());
                if (appliance != null) {
                    appliance.updateFrom(pojo, activeHouse);
                }
                else {
                    appliance = new ObservableAppliance(pojo, activeHouse);
                    cache.put(pojo.id(), appliance);
                }
                masterList.add(appliance);
            }
        });
    }
}