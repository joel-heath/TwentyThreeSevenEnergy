package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.HouseRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;

import java.util.List;

@Singleton
public class SwitchableHouseRepository implements HouseRepository {

    private final LocalHouseRepository localRepository;
    private final RemoteHouseRepository remoteRepository;
    private final SessionManager sessionManager;

    @Inject
    public SwitchableHouseRepository(LocalHouseRepository localRepository, RemoteHouseRepository remoteRepository, SessionManager sessionManager) {
        this.localRepository = localRepository;
        this.remoteRepository = remoteRepository;
        this.sessionManager = sessionManager;
    }

    private HouseRepository getActiveRepo() {
        return sessionManager.isLoggedIn() ? remoteRepository : localRepository;
    }

    @Override
    public House add() {
        return getActiveRepo().add();
    }

    @Override
    public House add(House house) {
        return getActiveRepo().add(house);
    }

    @Override
    public House get(Long houseId) {
        return getActiveRepo().get(houseId);
    }

    @Override
    public List<House> getAll() {
        return getActiveRepo().getAll();
    }

    @Override
    public House update(House house) {
        return getActiveRepo().update(house);
    }

    @Override
    public void delete(Long houseId) {
        getActiveRepo().delete(houseId);
    }

    @Override
    public List<Housemate> getHousemates(Long houseId) {
        return getActiveRepo().getHousemates(houseId);
    }

    @Override
    public Housemate getCurrentUserAsHousemate(Long houseId) {
        return getActiveRepo().getCurrentUserAsHousemate(houseId);
    }
}
