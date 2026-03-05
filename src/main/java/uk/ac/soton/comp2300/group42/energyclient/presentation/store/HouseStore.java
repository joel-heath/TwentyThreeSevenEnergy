package uk.ac.soton.comp2300.group42.energyclient.presentation.store;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.UIExecutor;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.HouseRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;

@Singleton
public class HouseStore {

    private final HouseRepository repository;
    private final Map<Long, ObservableHouse> cache;
    private final ObservableList<ObservableHouse> masterList;
    private final Executor uiExecutor;

    @Inject
    public HouseStore(HouseRepository repository, SessionManager sessionManager, @UIExecutor Executor uiExecutor) {
        this.repository = repository;
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

    public ObservableHouse add() {
        House pojo = repository.add();
        return getObservable(pojo);
    }

    public ObservableHouse add(House house) {
        House pojo = repository.add(house);
        return getObservable(pojo);
    }

    public ObservableHouse get(Long id) {
        House pojo = repository.get(id);
        return getObservable(pojo);
    }

    public ObservableList<ObservableHouse> getAll() {
        return masterList;
    }

    public ObservableHouse update(House house) {
        House pojo = repository.update(house);
        return getObservable(pojo);
    }

    public void delete(Long id) {
        repository.delete(id);
        cache.remove(id);
        uiExecutor.execute(() -> masterList.removeIf(h -> h.getId().equals(id)));
    }

    public void leave(Long id) {
        // TODO: repository.leave(id);
        cache.remove(id);
        uiExecutor.execute(() -> masterList.removeIf(h -> h.getId().equals(id)));
    }

    private ObservableHouse getObservable(House pojo) {
        ObservableHouse existing = cache.get(pojo.id());

        if (existing != null) {
            uiExecutor.execute(() -> existing.updateFrom(pojo));
            return existing;
        } else {
            ObservableHouse house = new ObservableHouse(pojo);
            cache.put(house.getId(), house);
            uiExecutor.execute(() -> masterList.add(house));
            return house;
        }
    }

    public void refreshAll() {
        List<House> pojos = repository.getAll();

        uiExecutor.execute(() -> {
            masterList.clear();
            for (House pojo : pojos) {
                ObservableHouse house = cache.get(pojo.id());
                if (house != null) {
                    house.updateFrom(pojo);
                }
                else {
                    house = new ObservableHouse(pojo);
                    cache.put(pojo.id(), house);
                }
                masterList.add(house);
            }
        });
    }
}