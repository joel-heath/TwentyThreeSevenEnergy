package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Activation;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.ActivationRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;

import java.util.List;

@Singleton
public class SwitchableActivationRepository implements ActivationRepository {

    private final LocalActivationRepository localRepository;
    private final RemoteActivationRepository remoteRepository;
    private final SessionManager sessionManager;

    @Inject
    public SwitchableActivationRepository(LocalActivationRepository localRepository, RemoteActivationRepository remoteRepository, SessionManager sessionManager) {
        this.localRepository = localRepository;
        this.remoteRepository = remoteRepository;
        this.sessionManager = sessionManager;
    }

    private ActivationRepository getActiveRepo() {
        return sessionManager.isLoggedIn() ? remoteRepository : localRepository;
    }

    @Override
    public Activation add(Activation activation) {
        return getActiveRepo().add(activation);
    }

    @Override
    public Activation get(Long houseId, Long activationId) {
        return getActiveRepo().get(houseId, activationId);
    }

    @Override
    public List<Activation> getAll(Long houseId) {
        return getActiveRepo().getAll(houseId);
    }

    @Override
    public Activation update(Activation activation) {
        return getActiveRepo().update(activation);
    }

    @Override
    public void delete(Long houseId, Long activationId) {
        getActiveRepo().delete(houseId, activationId);
    }
}
