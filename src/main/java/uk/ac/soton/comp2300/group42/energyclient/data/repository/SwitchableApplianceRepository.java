package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.ApplianceRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;

import java.util.List;

@Singleton
public class SwitchableApplianceRepository implements ApplianceRepository {

    private final LocalApplianceRepository localRepository;
    private final RemoteApplianceRepository remoteRepository;
    private final SessionManager sessionManager;

    @Inject
    public SwitchableApplianceRepository(LocalApplianceRepository localRepository, RemoteApplianceRepository remoteRepository, SessionManager sessionManager) {
        this.localRepository = localRepository;
        this.remoteRepository = remoteRepository;
        this.sessionManager = sessionManager;
    }

    private ApplianceRepository getActiveRepo() {
        return sessionManager.isLoggedIn() ? remoteRepository : localRepository;
    }

    @Override
    public Appliance add(Appliance appliance) {
        return getActiveRepo().add(appliance);
    }

    @Override
    public Appliance get(Long houseId, Long applianceId) {
        return getActiveRepo().get(houseId, applianceId);
    }

    @Override
    public List<Appliance> getAll(Long houseId) {
        return getActiveRepo().getAll(houseId);
    }

    @Override
    public Appliance update(Appliance appliance) {
        return getActiveRepo().update(appliance);
    }

    @Override
    public void delete(Long houseId, Long applianceId) {
        getActiveRepo().delete(houseId, applianceId);
    }
}
