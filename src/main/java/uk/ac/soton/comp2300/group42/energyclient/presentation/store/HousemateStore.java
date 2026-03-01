package uk.ac.soton.comp2300.group42.energyclient.presentation.store;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.HouseRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHousemate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

@Singleton
public class HousemateStore {

    private final HouseRepository repository;
    private final HouseStore houseStore;
    private final ObservablePreferences preferences;
    private final Map<Long, ObservableHousemate> cache;
    private final ObservableList<ObservableHousemate> masterList;

    @Inject
    public HousemateStore(HouseRepository repository, HouseStore houseStore, ObservablePreferences preferences) {
        this.repository = repository;
        this.houseStore = houseStore;
        this.preferences = preferences;
        this.cache = new WeakHashMap<>();
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
        Platform.runLater(() -> masterList.removeIf(h -> h.getId().equals(userId)));
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
            Platform.runLater(() -> existing.updateFrom(pojo, house));
            return existing;
        } else {
            ObservableHousemate housemate = new ObservableHousemate(pojo, house);
            cache.put(pojo.userId(), housemate);
            Platform.runLater(() -> masterList.add(housemate));
            return housemate;
        }
    }


    public ObservableList<ObservableHousemate> getAll() {
        return masterList;
    }

    public void refreshAll() {
        ObservableHouse activeHouse = preferences.getActiveHouse();

        List<Pair<ObservableHousemate, Optional<Housemate>>> pojos = repository
                .getHousemates(getActiveHouseId())
                .stream()
                .map(pojo -> {
                    ObservableHousemate housemate = cache.get(pojo.userId());
                    Optional<Housemate> returnedPojo;
                    if (housemate != null) {
                        returnedPojo = Optional.of(pojo);
                    }
                    else {
                        housemate = new ObservableHousemate(pojo, activeHouse);
                        cache.put(pojo.userId(), housemate);
                        returnedPojo = Optional.empty();
                    }
                    return new Pair<>(housemate, returnedPojo);
                })
                .toList();

        Platform.runLater(() -> {
            masterList.setAll(pojos.stream().map(Pair::getKey).toList());

            pojos.forEach(pair -> pair.getValue().ifPresent(
                    pojo -> pair.getKey().updateFrom(pojo, activeHouse)
            ));
        });
    }
}