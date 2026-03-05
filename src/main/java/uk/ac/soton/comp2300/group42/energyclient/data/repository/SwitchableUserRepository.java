package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.User;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.UserRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;

@Singleton
public class SwitchableUserRepository implements UserRepository {

    private final LocalUserRepository localRepository;
    private final RemoteUserRepository remoteRepository;
    private final SessionManager sessionManager;

    @Inject
    public SwitchableUserRepository(LocalUserRepository localRepository, RemoteUserRepository remoteRepository, SessionManager sessionManager) {
        this.localRepository = localRepository;
        this.remoteRepository = remoteRepository;
        this.sessionManager = sessionManager;
    }

    private UserRepository getActiveRepo() {
        return sessionManager.isLoggedIn() ? remoteRepository : localRepository;
    }

    @Override
    public User getCurrent() {
        return getActiveRepo().getCurrent();
    }

    @Override
    public Preferences getCurrentPreferences() {
        return getActiveRepo().getCurrentPreferences();
    }

    @Override
    public Preferences updateCurrentPreferences(Preferences preferences) {
        return getActiveRepo().updateCurrentPreferences(preferences);
    }

    @Override
    public User get(Long id) {
        return remoteRepository.get(id);
    }

    @Override
    public User updateMe(User user) {
        return getActiveRepo().updateMe(user);
    }

    @Override
    public void deleteMe(String password) {
        getActiveRepo().deleteMe(password);
    }
}
