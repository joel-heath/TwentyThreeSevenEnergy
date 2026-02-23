package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.HouseRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;

import java.util.List;
import java.util.Optional;

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
    public List<House> findHousesForCurrentUser() {
        return getActiveRepo().findHousesForCurrentUser();
    }

    @Override
    public Optional<House> findHouseById(Long houseId) {
        return getActiveRepo().findHouseById(houseId);
    }

    @Override
    public List<Housemate> findAllByHouseId(Long houseId) {
        return getActiveRepo().findAllByHouseId(houseId);
    }

    @Override
    public Optional<Housemate> findCurrentUserByHouseId(Long houseId) {
        return getActiveRepo().findCurrentUserByHouseId(houseId);
    }

    @Override
    public House createDefaultHouse() {
        return getActiveRepo().createDefaultHouse();
    }
}
